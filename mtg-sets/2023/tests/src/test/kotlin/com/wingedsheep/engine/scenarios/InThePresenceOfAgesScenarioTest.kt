package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.InThePresenceOfAges
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * In the Presence of Ages — {2}{G} Instant (LCI #192, common)
 *
 * "Reveal the top four cards of your library. You may put a creature card and/or a land card from
 * among them into your hand. Put the rest into your graveyard."
 *
 * Tests:
 *  - Two independent optional selections (creature first, then land from the remainder)
 *  - The chosen creature and land each go to hand; everything else goes to the graveyard
 *  - Both selections are optional (min = 0 each)
 *  - Declining both selections sends all four revealed cards to the graveyard
 */
class InThePresenceOfAgesScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + InThePresenceOfAges)
        return driver
    }

    test("selecting a creature and a land puts both in hand; the other two go to the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack the top 4 of the library (last call = top of library):
        //   position 4 (deepest): Grizzly Bears — filler, goes to graveyard
        //   position 3          : Grizzly Bears — filler, goes to graveyard
        //   position 2          : Forest        — the land card we will take
        //   position 1 (top)    : Grizzly Bears — the creature card we will take
        val filler1 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val filler2 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val land = driver.putCardOnTopOfLibrary(player, "Forest")
        val creature = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")

        val spell = driver.putCardInHand(player, "In the Presence of Ages")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 2)

        driver.castSpell(player, spell)
        driver.bothPass() // resolve → pauses on the creature selection

        // First decision: choose up to 1 creature card from the top 4.
        val creatureDecision = driver.pendingDecision as SelectCardsDecision
        creatureDecision.minSelections shouldBe 0
        creatureDecision.maxSelections shouldBe 1
        // Forest is a land, not a creature — it must not be selectable here.
        creatureDecision.options.contains(land) shouldBe false
        creatureDecision.options.contains(creature) shouldBe true

        driver.submitCardSelection(player, listOf(creature))

        // Second decision: choose up to 1 land card from the remaining three cards.
        val landDecision = driver.pendingDecision as SelectCardsDecision
        landDecision.minSelections shouldBe 0
        landDecision.maxSelections shouldBe 1
        // Only the Forest qualifies as a land in this step.
        landDecision.options.contains(land) shouldBe true
        // The already-chosen creature is gone from the pool; the two filler Bears are not land.
        landDecision.options.contains(filler1) shouldBe false
        landDecision.options.contains(filler2) shouldBe false

        driver.submitCardSelection(player, listOf(land))
        driver.isPaused shouldBe false

        // Both chosen cards arrived in hand.
        driver.getHand(player).contains(creature) shouldBe true
        driver.getHand(player).contains(land) shouldBe true
        // The two filler Grizzly Bears went to the graveyard.
        driver.getGraveyard(player).contains(filler1) shouldBe true
        driver.getGraveyard(player).contains(filler2) shouldBe true
    }

    test("declining both selections puts all four revealed cards into the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val c1 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val c2 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val land = driver.putCardOnTopOfLibrary(player, "Forest")
        val c3 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")

        val spell = driver.putCardInHand(player, "In the Presence of Ages")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 2)

        driver.castSpell(player, spell)
        driver.bothPass()

        // Decline the creature selection.
        val creatureDecision = driver.pendingDecision as SelectCardsDecision
        creatureDecision.minSelections shouldBe 0
        driver.submitCardSelection(player, emptyList())

        // Decline the land selection. The full four-card remainder is still in scope.
        val landDecision = driver.pendingDecision as SelectCardsDecision
        landDecision.minSelections shouldBe 0
        landDecision.options.contains(land) shouldBe true
        driver.submitCardSelection(player, emptyList())

        driver.isPaused shouldBe false

        // All four revealed cards ended up in the graveyard.
        val graveyard = driver.getGraveyard(player)
        graveyard.contains(c1) shouldBe true
        graveyard.contains(c2) shouldBe true
        graveyard.contains(land) shouldBe true
        graveyard.contains(c3) shouldBe true
        // None of them went to hand.
        val hand = driver.getHand(player)
        hand.contains(c1) shouldBe false
        hand.contains(c2) shouldBe false
        hand.contains(land) shouldBe false
        hand.contains(c3) shouldBe false
    }
})
