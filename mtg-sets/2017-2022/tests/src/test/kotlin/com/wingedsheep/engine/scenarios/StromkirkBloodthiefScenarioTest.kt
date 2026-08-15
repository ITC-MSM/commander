package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mid.cards.StromkirkBloodthief
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stromkirk Bloodthief — {2}{B} Vampire Rogue 2/2
 *
 * "At the beginning of your end step, if an opponent lost life this turn, put a +1/+1 counter on
 * target Vampire you control."
 *
 * Intervening-if end-step trigger. Proves the counter lands when an opponent lost life this turn,
 * and that with no life loss the trigger doesn't fire.
 */
class StromkirkBloodthiefScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(StromkirkBloodthief))
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("opponent lost life this turn: +1/+1 counter on a target Vampire at your end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bloodthief = driver.putCreatureOnBattlefield(me, "Stromkirk Bloodthief") // Vampire, 2/2
        plusCounters(driver, bloodthief) shouldBe 0

        // Make an opponent lose life this turn (real damage → life-loss event).
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, targets = listOf(opp)).isSuccess shouldBe true
        driver.bothPass()
        driver.getLifeTotal(opp) shouldBe 17

        // Advance to my end step; the trigger fires and wants a Vampire target.
        driver.passPriorityUntil(Step.END)
        var safety = 0
        while (safety < 20) {
            val pd = driver.pendingDecision
            when {
                pd is ChooseTargetsDecision -> driver.submitTargetSelection(me, listOf(bloodthief))
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
            safety++
        }

        plusCounters(driver, bloodthief) shouldBe 1
    }

    test("no opponent life loss: trigger does not fire, no counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!

        val bloodthief = driver.putCreatureOnBattlefield(me, "Stromkirk Bloodthief")

        // No damage dealt this turn — the intervening-if fails.
        driver.passPriorityUntil(Step.END)
        (driver.pendingDecision is ChooseTargetsDecision) shouldBe false

        plusCounters(driver, bloodthief) shouldBe 0
    }
})
