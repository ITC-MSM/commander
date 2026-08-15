package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.player.AdditionalPhasesComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Missing Out (DSK #136) — {1}{R} Enchantment Creature — Nightmare 2/3.
 *
 * "When this creature enters, discard a card, then draw a card.
 *  Delirium — Whenever this creature attacks for the first time each turn, if there are four or more
 *  card types among cards in your graveyard, untap target creature. After this phase, there is an
 *  additional combat phase."
 *
 * Exercises the ETB rummage (discard then draw), and the Delirium-gated first-attack trigger: it
 * untaps a target creature and queues a single additional combat phase only when Delirium is on,
 * and does nothing when Delirium is off.
 */
class FearOfMissingOutScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Fill the player's graveyard with four distinct card types to switch Delirium on. */
    fun GameTestDriver.enableDelirium(player: EntityId) {
        putCardInGraveyard(player, "Grizzly Bears")    // creature
        putCardInGraveyard(player, "Lightning Bolt")   // instant
        putCardInGraveyard(player, "Doom Blade")       // sorcery (per Spineseeker test corpus)
        putCardInGraveyard(player, "Test Enchantment") // enchantment
    }

    test("enters: discard a card, then draw a card") {
        val d = newDriver()
        val me = d.player1

        val toDiscard = d.putCardInHand(me, "Grizzly Bears")
        d.putCardOnTopOfLibrary(me, "Lightning Bolt") // the card we will draw
        val fomo = d.putCardInHand(me, "Fear of Missing Out")
        d.giveMana(me, Color.RED, 1)
        d.giveColorlessMana(me, 1)

        d.castSpell(me, fomo)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // ETB pauses to choose the card to discard.
        val discardPick = d.pendingDecision as? SelectCardsDecision
            ?: error("expected a discard SelectCardsDecision; got ${d.pendingDecision}")
        d.submitDecision(me, CardsSelectedResponse(discardPick.id, listOf(toDiscard)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The discarded card is in the graveyard and the drawn card is in hand.
        d.getGraveyard(me).map { d.getCardName(it) }.contains("Grizzly Bears") shouldBe true
        d.getHand(me).map { d.getCardName(it) }.contains("Lightning Bolt") shouldBe true
    }

    test("Delirium on: first attack untaps target creature and queues an additional combat phase") {
        val d = newDriver()
        val me = d.player1
        val opp = d.player2

        val fomo = d.putCreatureOnBattlefield(me, "Fear of Missing Out")
        d.removeSummoningSickness(fomo)
        d.enableDelirium(me)

        // A separate tapped creature we control to be the untap target.
        val tapped = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.tapPermanent(tapped)
        d.isTapped(tapped) shouldBe true

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(fomo), opp)

        // The Delirium trigger pauses to choose the untap target.
        var guard = 0
        while (d.pendingDecision !is ChooseTargetsDecision && guard++ < 20) {
            if (d.state.stack.isNotEmpty()) d.bothPass() else break
        }
        d.pendingDecision as? ChooseTargetsDecision
            ?: error("expected ChooseTargetsDecision for the untap target; got ${d.pendingDecision}")
        d.submitTargetSelection(me, listOf(tapped))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Target creature is untapped and an additional combat phase is queued.
        d.isTapped(tapped) shouldBe false
        val phases = d.state.getEntity(me)?.get<AdditionalPhasesComponent>()
        (phases != null && phases.phases.isNotEmpty()) shouldBe true
    }

    test("Delirium off: first attack does nothing (no target, no extra combat)") {
        val d = newDriver()
        val me = d.player1
        val opp = d.player2

        val fomo = d.putCreatureOnBattlefield(me, "Fear of Missing Out")
        d.removeSummoningSickness(fomo)
        // Only three card types in the graveyard — Delirium is OFF.
        d.putCardInGraveyard(me, "Grizzly Bears")
        d.putCardInGraveyard(me, "Lightning Bolt")
        d.putCardInGraveyard(me, "Doom Blade")

        val tapped = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.tapPermanent(tapped)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(fomo), opp)
        repeat(6) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        // No untap, no additional combat phase — the intervening-if failed.
        d.isTapped(tapped) shouldBe true
        val phases = d.state.getEntity(me)?.get<AdditionalPhasesComponent>()
        (phases == null || phases.phases.isEmpty()) shouldBe true
    }
})
