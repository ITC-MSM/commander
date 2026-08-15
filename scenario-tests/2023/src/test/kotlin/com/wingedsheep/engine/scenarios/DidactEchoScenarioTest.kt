package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.DidactEcho
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Didact Echo (LCI #53): {4}{U} 3/2 Creature — Spirit Cleric
 * "When this creature enters, draw a card.
 *  Descend 4 — This creature has flying as long as there are four or more permanent cards
 *  in your graveyard."
 *
 * Tests:
 *  - ETB trigger always draws one card.
 *  - With fewer than 4 permanent cards in the graveyard → no flying.
 *  - With four or more permanent cards in the graveyard → gains flying.
 */
class DidactEchoScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DidactEcho))
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0,
        )
        return driver
    }

    test("ETB trigger draws a card when Didact Echo enters the battlefield") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // Add Didact Echo to hand, then snapshot — so the accounting is purely
        // cast (−1) + ETB draw (+1) = net 0 against this baseline.
        val echo = driver.putCardInHand(player, "Didact Echo")
        val handBefore = driver.getHandSize(player)

        // Cast Didact Echo ({4}{U}).
        driver.giveColorlessMana(player, 4)
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, echo)
        // Spell resolves → ETB fires → draw trigger resolves.
        driver.bothPass()
        driver.bothPass() // draw trigger

        // Hand should be handBefore − 1 (Didact Echo cast) + 1 (ETB draw) = handBefore.
        driver.getHandSize(player) shouldBe handBefore
    }

    test("does not have flying when fewer than 4 permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val echo = driver.putCreatureOnBattlefield(player, "Didact Echo")

        // Only 3 permanent cards in the graveyard — one short of the Descend 4 threshold.
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        val projected = projector.project(driver.state)
        projected.hasKeyword(echo, Keyword.FLYING) shouldBe false
    }

    test("has flying when four or more permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val echo = driver.putCreatureOnBattlefield(player, "Didact Echo")

        // Exactly 4 permanent cards in the graveyard — meets the Descend 4 threshold.
        repeat(4) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        val projected = projector.project(driver.state)
        projected.hasKeyword(echo, Keyword.FLYING) shouldBe true
    }
})
