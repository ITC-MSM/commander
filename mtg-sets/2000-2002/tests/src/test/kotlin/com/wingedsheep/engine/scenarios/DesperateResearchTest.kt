package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.DesperateResearch
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Desperate Research (INV #100) — Invasion engine gap #10, the "name a card" half.
 *
 * "Choose a card name other than a basic land card name. Reveal the top seven cards of
 * your library and put all of them with that name into your hand. Exile the rest."
 */
class DesperateResearchTest : FunSpec({

    test("named cards go to hand, the rest are exiled") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DesperateResearch))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Swamp" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        // Seed the top seven: three Lightning Bolts (named) + four Stoke the Flames (the rest).
        repeat(4) { driver.putCardOnTopOfLibrary(you, "Stoke the Flames") }
        repeat(3) { driver.putCardOnTopOfLibrary(you, "Lightning Bolt") }

        val dr = driver.putCardInHand(you, "Desperate Research")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, dr)
        driver.bothPass()

        // Resolving the spell pauses to name a card.
        val decision = driver.pendingDecision
        decision.shouldNotBe(null)
        decision as ChooseOptionDecision
        // Basic land card names are excluded from the choices.
        decision.options shouldNotContain "Plains"
        val boltIndex = decision.options.indexOf("Lightning Bolt")
        boltIndex shouldNotBe -1
        driver.submitDecision(you, OptionChosenResponse(decision.id, boltIndex))

        driver.isPaused shouldBe false
        // The three Lightning Bolts are now in hand; the four Stoke the Flames are exiled.
        driver.findCardsInHand(you, "Lightning Bolt").size shouldBe 3
        driver.getExileCardNames(you).count { it == "Stoke the Flames" } shouldBe 4
        driver.getExileCardNames(you) shouldNotContain "Lightning Bolt"
    }

    test("naming a card absent from the top seven exiles all of them") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DesperateResearch))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Swamp" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        repeat(7) { driver.putCardOnTopOfLibrary(you, "Stoke the Flames") }

        val dr = driver.putCardInHand(you, "Desperate Research")
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, dr)
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(you, OptionChosenResponse(decision.id, decision.options.indexOf("Lightning Bolt")))

        driver.isPaused shouldBe false
        driver.findCardsInHand(you, "Lightning Bolt").size shouldBe 0
        driver.getExileCardNames(you).count { it == "Stoke the Flames" } shouldBe 7
    }
})
