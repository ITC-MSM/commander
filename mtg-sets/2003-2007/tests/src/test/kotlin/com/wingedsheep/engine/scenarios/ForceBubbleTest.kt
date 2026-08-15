package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Force Bubble:
 * {2}{W}{W} Enchantment
 * If damage would be dealt to you, put that many depletion counters on Force Bubble instead.
 * When there are four or more depletion counters on Force Bubble, sacrifice it.
 * At the beginning of each end step, remove all depletion counters from Force Bubble.
 */
class ForceBubbleTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun getDepletionCounters(driver: GameTestDriver, entityId: EntityId): Int {
        return driver.state.getEntity(entityId)
            ?.get<CountersComponent>()
            ?.getCount(CounterType.DEPLETION) ?: 0
    }

    test("prevents damage and adds depletion counters") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bubble = driver.putPermanentOnBattlefield(activePlayer, "Force Bubble")

        // Opponent casts Lightning Bolt targeting controller
        driver.passPriority(activePlayer)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt, listOf(activePlayer))
        driver.bothPass()

        // Life should be unchanged — damage was replaced
        driver.getLifeTotal(activePlayer) shouldBe 20

        // Force Bubble should have 3 depletion counters
        getDepletionCounters(driver, bubble) shouldBe 3
    }

    test("sacrificed when 4 or more depletion counters") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bubble = driver.putPermanentOnBattlefield(activePlayer, "Force Bubble")

        // Deal 4+ damage at once — should trigger sacrifice
        driver.passPriority(activePlayer)
        val bolt1 = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt1, listOf(activePlayer))
        driver.bothPass() // 3 counters

        // Deal 1 more damage to reach 4 counters
        driver.passPriority(activePlayer)
        val bolt2 = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt2, listOf(activePlayer))
        driver.bothPass() // 6 counters — triggers sacrifice

        // Force Bubble should be sacrificed
        driver.findPermanent(activePlayer, "Force Bubble") shouldBe null

        // Life should still be 20 — both were prevented
        driver.getLifeTotal(activePlayer) shouldBe 20
    }

    test("end step removes all depletion counters") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Mountain" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bubble = driver.putPermanentOnBattlefield(activePlayer, "Force Bubble")

        // Deal 3 damage (below threshold)
        driver.passPriority(activePlayer)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt, listOf(activePlayer))
        driver.bothPass()

        getDepletionCounters(driver, bubble) shouldBe 3

        // Advance to end step — counters should be removed
        driver.passPriorityUntil(Step.END, maxPasses = 200)
        driver.bothPass() // resolve end step trigger

        getDepletionCounters(driver, bubble) shouldBe 0

        // Bubble should still be on battlefield
        driver.findPermanent(activePlayer, "Force Bubble") shouldBe bubble
    }
})
