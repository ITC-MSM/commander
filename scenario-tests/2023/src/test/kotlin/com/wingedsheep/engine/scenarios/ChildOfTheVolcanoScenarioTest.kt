package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.ChildOfTheVolcano
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Child of the Volcano (LCI #140): {3}{R} 3/3 Elemental
 *
 * Trample
 * "At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature."
 *
 * Tests:
 * 1. No +1/+1 counter is placed at end step when the controller has not descended.
 * 2. A +1/+1 counter is placed at end step when the controller has descended this turn.
 */
class ChildOfTheVolcanoScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ChildOfTheVolcano))
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )
        return driver
    }

    /** Move a permanent card to the graveyard to trigger the descend counter (CR 700.11). */
    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("no +1/+1 counter is placed at end step when the controller has not descended") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val child = driver.putCreatureOnBattlefield(player, "Child of the Volcano")

        val countersBefore = plusOneCounters(driver, child)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        plusOneCounters(driver, child) shouldBe countersBefore
    }

    test("a +1/+1 counter is placed at end step when the controller has descended this turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val child = driver.putCreatureOnBattlefield(player, "Child of the Volcano")

        // Put a permanent (creature) card into the graveyard — this satisfies CR 700.11 descend.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(bears)

        val countersBefore = plusOneCounters(driver, child)
        driver.passPriorityUntil(Step.END)
        driver.bothPass() // EOT trigger fires and resolves, placing the counter

        plusOneCounters(driver, child) shouldBe countersBefore + 1
    }
})
