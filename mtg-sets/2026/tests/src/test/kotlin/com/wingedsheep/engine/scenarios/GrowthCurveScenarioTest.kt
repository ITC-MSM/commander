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
 * Scenario test for Growth Curve (SOS #193) — {G}{U} Sorcery.
 *
 * "Put a +1/+1 counter on target creature you control, then double the number of +1/+1 counters
 *  on that creature."
 *
 * Composed from [com.wingedsheep.sdk.dsl.Effects.AddCounters] then
 * [com.wingedsheep.sdk.dsl.Effects.DoubleCounters]: the freshly-added counter is included in the
 * doubling, so N existing counters become (N + 1) * 2.
 */
class GrowthCurveScenarioTest : FunSpec({

    fun plusCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("adds one +1/+1 counter then doubles: a counterless creature ends with two counters") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val curve = driver.putCardInHand(player, "Growth Curve")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)

        driver.castSpell(player, curve, targets = listOf(bears))
        driver.bothPass()

        // 0 existing -> +1 -> 1 -> double -> 2 counters.
        plusCounters(driver, bears) shouldBe 2
    }

    test("existing counters are doubled together with the new one") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.addComponent(bears, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 2)))
        val curve = driver.putCardInHand(player, "Growth Curve")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)

        driver.castSpell(player, curve, targets = listOf(bears))
        driver.bothPass()

        // 2 existing -> +1 -> 3 -> double -> 6 counters.
        plusCounters(driver, bears) shouldBe 6
    }
})
