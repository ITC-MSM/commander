package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Drag to the Roots (DSK #213) — {2}{B}{G} Instant.
 *
 *  - Delirium — This spell costs {2} less to cast as long as there are four or more card types
 *    among cards in your graveyard.
 *  - Destroy target nonland permanent.
 *
 * The reduction is a fixed {2} generic reduction gated by the four-card-type delirium condition,
 * so the effective cost is the full {2}{B}{G} (CMC 4) without delirium and {B}{G} (CMC 2) with it.
 */
class DragToTheRootsScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("costs full {2}{B}{G} without delirium and {B}{G} with four card types in graveyard") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val calculator = CostCalculator(d.cardRegistry)
        val card = d.cardRegistry.requireCard("Drag to the Roots")

        // No delirium yet — full cost.
        val full = calculator.calculateEffectiveCost(d.state, card, you)
        full.genericAmount shouldBe 2
        full.cmc shouldBe 4

        // Three card types only (creature, instant, sorcery) — still no reduction.
        d.putCardInGraveyard(you, "Centaur Courser") // creature
        d.putCardInGraveyard(you, "Lightning Bolt")  // instant
        d.putCardInGraveyard(you, "Doom Blade")      // (instant) — keep at three distinct types
        val threeTypes = calculator.calculateEffectiveCost(d.state, card, you)
        threeTypes.genericAmount shouldBe 2
        threeTypes.cmc shouldBe 4

        // Add an enchantment and a land → four+ card types → {2} reduction.
        d.putCardInGraveyard(you, "Test Enchantment")
        d.putCardInGraveyard(you, "Forest")
        val withDelirium = calculator.calculateEffectiveCost(d.state, card, you)
        withDelirium.genericAmount shouldBe 0
        withDelirium.cmc shouldBe 2
    }

    test("destroys target nonland permanent") {
        val d = driver()
        val you = d.activePlayer!!
        val opp = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = d.putCreatureOnBattlefield(opp, "Centaur Courser")
        val spell = d.putCardInHand(you, "Drag to the Roots")
        d.giveMana(you, Color.BLACK, 1)
        d.giveMana(you, Color.GREEN, 1)
        d.giveColorlessMana(you, 2)
        d.castSpellWithTargets(you, spell, listOf(entityIdToChosenTarget(d.state, victim)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.state.getZone(com.wingedsheep.engine.state.ZoneKey(opp, Zone.GRAVEYARD)).contains(victim) shouldBe true
        d.getPermanents(opp).contains(victim) shouldBe false
    }
})
