package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.PlayWithFixedAlternativeManaCostComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tla.cards.AvatarsWrath
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Avatar's Wrath (TLA) — {2}{W}{W} — Sorcery — Rare.
 *
 * - "Choose up to one target creature, then airbend all other creatures." — reuses
 *   [Effects.AirbendAll] with `excludeChosenTargets = true`, so the chosen target is spared and
 *   every other creature is exiled with a fixed-{2} recast granted to its owner.
 * - "Until your next turn, your opponents can't cast spells from anywhere other than their hands."
 *   — a per-opponent, until-your-next-turn restriction (CantCastFromNonHandZonesComponent) that
 *   leaves hand casts alone but blocks graveyard/flashback casts.
 * - "Exile Avatar's Wrath." — the resolving sorcery self-exiles instead of going to the graveyard.
 */
class AvatarsWrathScenarioTest : FunSpec({

    // Instant with flashback, used to prove the opponent can still cast from hand but not from
    // their graveyard while the cast-zone restriction is active.
    val flashbackInstant = card("Flashback Draw") {
        manaCost = "{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        oracleText = "Draw a card.\nFlashback {R}"
        spell { effect = Effects.DrawCards(1) }
        keywordAbility(KeywordAbility.flashback("{R}"))
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AvatarsWrath, flashbackInstant))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingLife = 20)
        return driver
    }

    /**
     * The "cast this card" legal action for [player], or null if they may not cast it now.
     * Matches on the underlying [CastSpell] action's cardId regardless of `actionType`, so it
     * finds both plain casts (`"CastSpell"`) and flashback casts (`"CastWithFlashback"`, whose
     * `action` is still a [CastSpell]).
     */
    fun castAction(driver: GameTestDriver, player: EntityId, cardId: EntityId): LegalAction? =
        LegalActionEnumerator.create(driver.cardRegistry)
            .enumerate(driver.state, player, EnumerationMode.FULL)
            .firstOrNull { (it.action as? CastSpell)?.cardId == cardId }

    /** Resolve the stack (and any resolution decisions) without advancing past the current turn. */
    fun GameTestDriver.resolveStack() {
        var guard = 0
        while ((state.pendingDecision != null || state.stack.isNotEmpty()) && guard < 50) {
            bothPass()
            guard++
        }
    }

    fun GameTestDriver.giveWrathMana(player: EntityId) {
        giveMana(player, Color.WHITE, 2)
        giveColorlessMana(player, 2)
    }

    test("airbends every creature except the chosen target; each exiled one's owner may recast for {2}") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spared = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val myOther = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val oppCreature = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val wrath = driver.putCardInHand(me, "Avatar's Wrath")
        driver.giveWrathMana(me)

        driver.submitSuccess(
            CastSpell(
                playerId = me,
                cardId = wrath,
                targets = listOf(ChosenTarget.Permanent(spared)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )
        driver.resolveStack()

        // The chosen target is spared and stays on the battlefield.
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)) shouldContain spared

        // Every other creature is airbended into its own owner's exile...
        driver.state.getZone(ZoneKey(me, Zone.EXILE)) shouldContain myOther
        driver.state.getZone(ZoneKey(opp, Zone.EXILE)) shouldContain oppCreature
        driver.state.getZone(ZoneKey(me, Zone.BATTLEFIELD)) shouldNotContain myOther
        driver.state.getZone(ZoneKey(opp, Zone.BATTLEFIELD)) shouldNotContain oppCreature

        // ...and each exiled creature's owner is granted a fixed-{2} recast permission.
        driver.state.getEntity(myOther)?.get<PlayWithFixedAlternativeManaCostComponent>()
            ?.fixedCost?.toString() shouldBe "{2}"
        driver.state.getEntity(oppCreature)?.get<PlayWithFixedAlternativeManaCostComponent>()
            ?.fixedCost?.toString() shouldBe "{2}"

        // I (the caster) am unrestricted, so I may actually recast my exiled creature for {2} now.
        castAction(driver, me, myOther)?.manaCostString shouldBe "{2}"
        // The opponent, however, can't cast from exile (a non-hand zone) until my next turn — the
        // same restriction Avatar's Wrath imposes below — even though the {2} grant exists.
        castAction(driver, opp, oppCreature).shouldBeNull()
    }

    test("opponents may still cast from hand but not from graveyard until your next turn") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent has an instant in hand and an identical one in their graveyard (flashback).
        val handInstant = driver.putCardInHand(opp, "Flashback Draw")
        val graveyardInstant = driver.putCardInGraveyard(opp, "Flashback Draw")
        driver.giveMana(opp, Color.RED, 2)

        // Before Avatar's Wrath: the opponent may cast the flashback card from their graveyard.
        castAction(driver, opp, graveyardInstant).shouldNotBeNull()

        val spared = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val wrath = driver.putCardInHand(me, "Avatar's Wrath")
        driver.giveWrathMana(me)
        driver.submitSuccess(
            CastSpell(
                playerId = me,
                cardId = wrath,
                targets = listOf(ChosenTarget.Permanent(spared)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )
        driver.resolveStack()

        // Hand cast is still legal; the graveyard (flashback) cast is now suppressed.
        castAction(driver, opp, handInstant).shouldNotBeNull()
        castAction(driver, opp, graveyardInstant).shouldBeNull()

        // And the cast handler rejects the graveyard cast authoritatively. Hand priority to the
        // opponent first so the rejection is the cast-zone restriction, not a lack of priority.
        driver.passPriority(me)
        driver.submitExpectFailure(
            CastSpell(
                playerId = opp,
                cardId = graveyardInstant,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )

        // The restriction lifts on the caster's next turn.
        driver.advanceToMyNextMain(me)
        castAction(driver, opp, graveyardInstant).shouldNotBeNull()
    }

    test("Avatar's Wrath exiles itself instead of going to the graveyard") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spared = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val wrath = driver.putCardInHand(me, "Avatar's Wrath")
        driver.giveWrathMana(me)
        driver.submitSuccess(
            CastSpell(
                playerId = me,
                cardId = wrath,
                targets = listOf(ChosenTarget.Permanent(spared)),
                paymentStrategy = PaymentStrategy.AutoPay
            )
        )
        driver.resolveStack()

        driver.state.getZone(ZoneKey(me, Zone.EXILE)) shouldContain wrath
        driver.state.getZone(ZoneKey(me, Zone.GRAVEYARD)) shouldNotContain wrath
    }
})

/** Advance from the current (my) turn to my next precombat main phase. */
private fun GameTestDriver.advanceToMyNextMain(me: EntityId) {
    val startTurn = state.turnNumber
    do {
        passPriorityUntil(Step.END)
        bothPass()
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    } while (activePlayer != me || state.turnNumber == startTurn)
}
