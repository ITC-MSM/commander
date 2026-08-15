package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.MoltenCollapse
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Molten Collapse — {B}{R} Sorcery
 *
 * "Choose one. If you descended this turn, you may choose both instead.
 *  • Destroy target creature or planeswalker.
 *  • Destroy target noncreature, nonland permanent with mana value 1 or less."
 *
 * Pins the cast-time conditional modal count: the floor stays "choose one", and the cap is
 * 2 only when the caster descended this turn (CR 700.11 — a permanent card was put into
 * their graveyard from anywhere).
 *
 * Modes: 0 = destroy target creature or planeswalker,
 *        1 = destroy target noncreature, nonland permanent with mana value 1 or less.
 */
class MoltenCollapseScenarioTest : FunSpec({

    // A noncreature, nonland permanent with mana value 1 — a legal mode-1 target.
    val Trinket: CardDefinition = CardDefinition.artifact(
        name = "Test Trinket",
        manaCost = ManaCost.parse("{1}")
    )

    // A noncreature, nonland permanent with mana value 3 — NOT a legal mode-1 target.
    val Relic: CardDefinition = CardDefinition.artifact(
        name = "Test Relic",
        manaCost = ManaCost.parse("{3}")
    )

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(MoltenCollapse)
        d.registerCard(Trinket)
        d.registerCard(Relic)
        return d
    }

    /** Move a permanent card to the graveyard to trigger descend (CR 700.11). */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun castSetup(d: GameTestDriver): Pair<EntityId, EntityId> {
        d.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveMana(p1, Color.BLACK, 1)
        d.giveMana(p1, Color.RED, 1)
        return p1 to d.getOpponent(p1)
    }

    test("not descended — choosing both modes is illegal (effective max is one)") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")
        val trinket = d.putPermanentOnBattlefield(p2, "Test Trinket")

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature), ChosenTarget.Permanent(trinket)),
            chosenModes = listOf(0, 1),
            modeTargetsOrdered = listOf(
                listOf(ChosenTarget.Permanent(creature)),
                listOf(ChosenTarget.Permanent(trinket))
            )
        ))

        result.isSuccess shouldBe false
    }

    test("not descended — choose mode 0 destroys target creature") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature)),
            chosenModes = listOf(0),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(creature)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
    }

    test("not descended — choose mode 1 destroys a MV<=1 noncreature nonland permanent") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val trinket = d.putPermanentOnBattlefield(p2, "Test Trinket")

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(trinket)),
            chosenModes = listOf(1),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(trinket)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Test Trinket").shouldBeNull()
    }

    test("mode 1 cannot target a permanent with mana value greater than 1") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val relic = d.putPermanentOnBattlefield(p2, "Test Relic") // MV 3

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(relic)),
            chosenModes = listOf(1),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(relic)))
        ))

        result.isSuccess shouldBe false
    }

    test("descended this turn — may choose both: destroy the creature AND the MV<=1 permanent") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")
        val trinket = d.putPermanentOnBattlefield(p2, "Test Trinket")

        // Descend: move a permanent card (creature in hand) to the graveyard this turn.
        val handBear = d.putCardInHand(p1, "Grizzly Bears")
        d.descend(handBear)

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature), ChosenTarget.Permanent(trinket)),
            chosenModes = listOf(0, 1),
            modeTargetsOrdered = listOf(
                listOf(ChosenTarget.Permanent(creature)),
                listOf(ChosenTarget.Permanent(trinket))
            )
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
        d.findPermanent(p2, "Test Trinket").shouldBeNull()
    }

    test("descended this turn — may still choose only one mode") {
        val d = driver()
        val (p1, p2) = castSetup(d)

        val creature = d.putCreatureOnBattlefield(p2, "Centaur Courser")
        val trinket = d.putPermanentOnBattlefield(p2, "Test Trinket")

        val handBear = d.putCardInHand(p1, "Grizzly Bears")
        d.descend(handBear)

        val spell = d.putCardInHand(p1, "Molten Collapse")
        val result = d.submit(CastSpell(
            playerId = p1,
            cardId = spell,
            targets = listOf(ChosenTarget.Permanent(creature)),
            chosenModes = listOf(0),
            modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(creature)))
        ))
        if (!result.isSuccess) throw AssertionError("cast failed: ${result.error}")

        d.bothPass()
        d.findPermanent(p2, "Centaur Courser").shouldBeNull()
        // The unchosen mode leaves the trinket alone.
        d.findPermanent(p2, "Test Trinket").shouldNotBeNull()
    }
})
