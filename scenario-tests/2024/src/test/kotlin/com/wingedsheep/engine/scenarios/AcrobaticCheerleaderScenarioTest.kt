package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Acrobatic Cheerleader (DSK #1) — {1}{W} 2/2 Creature — Human Survivor.
 *
 * "Survival — At the beginning of your second main phase, if this creature is tapped, put a
 *  flying counter on it. This ability triggers only once."
 *
 * Exercises the `triggersOnce` lifetime cap: the Survival trigger places a flying counter exactly
 * once while the creature stays on the battlefield, even if it is tapped at a later second main.
 */
class AcrobaticCheerleaderScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun flyingCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.FLYING) ?: 0

    test("puts a flying counter at the second main phase while tapped") {
        val driver = newDriver()
        val cheerleader = driver.putCreatureOnBattlefield(driver.player1, "Acrobatic Cheerleader")
        driver.tapPermanent(cheerleader)

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.bothPass()

        flyingCounters(driver, cheerleader) shouldBe 1
    }

    test("does not place a counter if untapped at the second main phase") {
        val driver = newDriver()
        val cheerleader = driver.putCreatureOnBattlefield(driver.player1, "Acrobatic Cheerleader")
        // Left untapped — the intervening-if (this creature is tapped) is false.

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        flyingCounters(driver, cheerleader) shouldBe 0
    }

    test("triggers only once — no second flying counter on a later turn") {
        val driver = newDriver()
        val cheerleader = driver.putCreatureOnBattlefield(driver.player1, "Acrobatic Cheerleader")
        driver.tapPermanent(cheerleader)

        // First controller second main: the ability fires once.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.bothPass()
        flyingCounters(driver, cheerleader) shouldBe 1

        // Advance to the controller's next turn (turn 3) precombat main, then re-tap so the
        // intervening-if would again be satisfied at the second main.
        driver.passPriorityUntil(Step.CLEANUP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's turn 2
        driver.passPriorityUntil(Step.CLEANUP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // controller's turn 3
        driver.tapPermanent(cheerleader)
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.bothPass()

        // Still exactly one — "This ability triggers only once" capped the lifetime.
        flyingCounters(driver, cheerleader) shouldBe 1
    }
})
