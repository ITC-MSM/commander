package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Friendly Teddy (DSK #247) — {2} Artifact Creature — Bear Toy 2/2.
 *
 * "When this creature dies, each player draws a card."
 *
 * Exercises the dies trigger fanning a single card draw over every player
 * (Effects.DrawCards(1, Player.Each)).
 */
class FriendlyTeddyScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("when Friendly Teddy dies, each player draws a card") {
        val driver = newDriver()
        val player = driver.player1
        val opponent = driver.player2

        val teddy = driver.putCreatureOnBattlefield(player, "Friendly Teddy")
        val handBefore = driver.getHandSize(player)
        val oppHandBefore = driver.getHandSize(opponent)

        // Destroy the Teddy via a Lightning Bolt to the face... actually bolt the 2/2 directly.
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, targets = listOf(teddy)).isSuccess shouldBe true
        driver.bothPass() // resolve the bolt — Teddy dies, queuing the dies trigger
        driver.bothPass() // resolve the dies trigger

        driver.isPaused shouldBe false
        driver.findPermanent(player, "Friendly Teddy") shouldBe null

        // Both players drew exactly one card from the dies trigger.
        driver.getHandSize(player) shouldBe handBefore + 1
        driver.getHandSize(opponent) shouldBe oppHandBefore + 1
    }
})
