package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Giant Beaver (OTJ).
 *
 * Oracle: "Vigilance. Whenever this creature attacks while saddled, put a +1/+1 counter on
 * target creature that saddled it this turn. Saddle 3"
 *
 * The "while saddled" gate is an intervening-if; the target is restricted to creatures that
 * saddled this Mount this turn (source-relative CrewSaddleContributorsComponent).
 */
class GiantBeaverScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("attacking while saddled puts a +1/+1 counter on a creature that saddled it") {
        val driver = newDriver()
        val beaver = driver.putCreatureOnBattlefield(driver.player1, "Giant Beaver")
        // Saddle 3 needs total power >= 3; two Grizzly Bears (power 2 each) suffice.
        val saddlerA = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val saddlerB = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(beaver)

        driver.submitSuccess(SaddleMount(driver.player1, beaver, listOf(saddlerA, saddlerB)))
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(beaver), driver.player2)

        // The attack trigger asks for a target among the creatures that saddled it.
        driver.submitTargetSelection(driver.player1, listOf(saddlerA))
        driver.bothPass()

        driver.plusOneCounters(saddlerA) shouldBe 1
        driver.plusOneCounters(saddlerB) shouldBe 0
    }

    test("attacking while not saddled does not trigger the counter") {
        val driver = newDriver()
        val beaver = driver.putCreatureOnBattlefield(driver.player1, "Giant Beaver")
        val bystander = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(beaver)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(driver.player1, listOf(beaver), driver.player2)
        driver.bothPass()

        driver.plusOneCounters(bystander) shouldBe 0
    }
})
