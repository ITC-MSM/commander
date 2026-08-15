package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.IzzetChronarch
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Izzet Chronarch: When this creature enters, return target instant or sorcery card from your
 * graveyard to your hand. Casting it with a Lightning Bolt (instant) in the graveyard returns
 * that Bolt to hand on the ETB trigger.
 */
class IzzetChronarchScenarioTest : FunSpec({

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(IzzetChronarch)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("Izzet Chronarch ETB returns an instant from the graveyard to hand") {
        val driver = setup()
        val player = driver.activePlayer!!

        val bolt = driver.putCardInGraveyard(player, "Lightning Bolt")
        driver.getGraveyardCardNames(player).contains("Lightning Bolt") shouldBe true

        val chronarch = driver.putCardInHand(player, "Izzet Chronarch")
        driver.giveMana(player, Color.BLUE, 3)
        driver.giveMana(player, Color.RED, 2) // {3}{U}{R}

        driver.castSpell(player, chronarch).isSuccess shouldBe true
        // Resolve the creature spell so it enters and the ETB trigger goes on the stack.
        driver.bothPass()

        // The ETB trigger prompts to choose the instant/sorcery target in the graveyard.
        if (driver.state.pendingDecision != null) {
            driver.submitTargetSelection(player, listOf(bolt))
        }
        // Resolve the ETB trigger.
        driver.bothPass()

        driver.findPermanent(player, "Izzet Chronarch") shouldNotBe null
        driver.findCardInHand(player, "Lightning Bolt") shouldNotBe null
        driver.getGraveyardCardNames(player).contains("Lightning Bolt") shouldBe false
    }
})
