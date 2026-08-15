package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.DreambornMuse
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Dreamborn Muse:
 * {2}{U}{U} Creature — Spirit 2/2
 * At the beginning of each player's upkeep, that player mills X cards,
 * where X is the number of cards in their hand.
 */
class DreambornMuseTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DreambornMuse))
        return driver
    }

    fun reduceHandTo(driver: GameTestDriver, playerId: EntityId, count: Int) {
        val hand = driver.getHand(playerId)
        if (hand.size <= count) return
        val keep = hand.take(count)
        val move = hand.drop(count)
        val handKey = ZoneKey(playerId, Zone.HAND)
        val libraryKey = ZoneKey(playerId, Zone.LIBRARY)
        val newZones = driver.state.zones.toMutableMap()
        newZones[handKey] = keep
        newZones[libraryKey] = move + (newZones[libraryKey] ?: emptyList())
        driver.replaceState(driver.state.copy(zones = newZones))
    }

    test("mills opponent equal to their hand size on their upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(controller, "Dreamborn Muse")

        // Set opponent's hand to exactly 3 cards
        reduceHandTo(driver, opponent, 3)
        val oppHandSize = driver.getHandSize(opponent)
        oppHandSize shouldBe 3

        val graveyardBefore = driver.getGraveyard(opponent).size

        // Advance to opponent's upkeep
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe opponent

        // Trigger should be on the stack
        driver.stackSize shouldBe 1
        driver.bothPass()

        // Opponent should have milled 3 cards (hand size before draw)
        // Note: the trigger checks hand size at resolution
        val graveyardAfter = driver.getGraveyard(opponent).size
        graveyardAfter shouldBe graveyardBefore + 3
    }

    test("mills controller on their own upkeep too") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(controller, "Dreamborn Muse")

        // Set controller's hand to 2 cards
        reduceHandTo(driver, controller, 2)

        val graveyardBefore = driver.getGraveyard(controller).size

        // Advance to controller's next upkeep
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != controller) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.activePlayer shouldBe controller

        driver.stackSize shouldBe 1
        driver.bothPass()

        val graveyardAfter = driver.getGraveyard(controller).size
        graveyardAfter shouldBe graveyardBefore + 2
    }

    test("mills zero when hand is empty") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(controller, "Dreamborn Muse")

        // Empty opponent's hand
        reduceHandTo(driver, opponent, 0)

        val graveyardBefore = driver.getGraveyard(opponent).size

        // Advance to opponent's upkeep
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        driver.activePlayer shouldBe opponent

        // Trigger fires but mills 0
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.getGraveyard(opponent).size shouldBe graveyardBefore
    }
})
