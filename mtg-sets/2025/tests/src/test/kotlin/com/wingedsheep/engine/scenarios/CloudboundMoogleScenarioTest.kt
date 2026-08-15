package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
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
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Cloudbound Moogle (FIN #11).
 *
 * Cloudbound Moogle {3}{W} {W} Creature — Moogle 2/3
 * Flying
 * When this creature enters, put a +1/+1 counter on target creature.
 * Plainscycling {2}
 */
class CloudboundMoogleScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("ETB puts a +1/+1 counter on a target creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(active, "Grizzly Bears")

        val moogle = driver.putCardInHand(active, "Cloudbound Moogle")
        driver.giveMana(active, Color.WHITE, 2)
        driver.giveColorlessMana(active, 3)

        driver.castSpell(active, moogle).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; ETB trigger goes on the stack and asks for a target

        val moogleId = driver.findPermanent(active, "Cloudbound Moogle")
        moogleId shouldNotBe null

        val decision = driver.pendingDecision
        (decision is ChooseTargetsDecision) shouldBe true
        driver.submitTargetSelection(active, listOf(bears))
        driver.bothPass() // resolve the ETB trigger

        plusCounters(driver, bears) shouldBe 1
    }
})
