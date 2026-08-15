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
 * Beastie Beatdown (DSK #210) — {R}{G} Sorcery.
 *
 * "Choose target creature you control and target creature an opponent controls.
 *  Delirium — If there are four or more card types among cards in your graveyard, put two +1/+1
 *  counters on the creature you control.
 *  The creature you control deals damage equal to its power to the creature an opponent controls."
 *
 * A one-sided "fight": only the controlled creature deals damage. The Delirium counters are placed
 * before the damage step, so the buffed power decides the result. Composes ConditionalEffect
 * (Delirium-gated AddCounters) + DealDamage(targetPower(0), source = controlled creature); no new SDK.
 */
class BeastieBeatdownScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("no delirium: 2-power creature deals 2 damage, the 2/3 target survives") {
        val driver = newDriver()
        val yours = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // 2/2
        val theirs = driver.putCreatureOnBattlefield(driver.player2, "Minotaur Warrior") // 2/3

        val beatdown = driver.putCardInHand(driver.player1, "Beastie Beatdown")
        driver.giveMana(driver.player1, Color.RED, 1)
        driver.giveMana(driver.player1, Color.GREEN, 1)
        driver.castSpell(driver.player1, beatdown, listOf(yours, theirs)).isSuccess shouldBe true
        driver.bothPass()

        // No delirium -> no counters; 2 damage to a toughness-3 creature is non-lethal.
        plusCounters(driver, yours) shouldBe 0
        driver.findPermanent(driver.player2, "Minotaur Warrior") shouldBe theirs
    }

    test("delirium active: two +1/+1 counters land first, so 4 damage destroys the 2/3 target") {
        val driver = newDriver()
        val yours = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // 2/2 -> 4/4
        val theirs = driver.putCreatureOnBattlefield(driver.player2, "Minotaur Warrior") // 2/3

        // Four card types in your graveyard: creature, instant, sorcery, enchantment.
        driver.putCardInGraveyard(driver.player1, "Grizzly Bears")
        driver.putCardInGraveyard(driver.player1, "Lightning Bolt")
        driver.putCardInGraveyard(driver.player1, "Careful Study")
        driver.putCardInGraveyard(driver.player1, "Test Enchantment")

        val beatdown = driver.putCardInHand(driver.player1, "Beastie Beatdown")
        driver.giveMana(driver.player1, Color.RED, 1)
        driver.giveMana(driver.player1, Color.GREEN, 1)
        driver.castSpell(driver.player1, beatdown, listOf(yours, theirs)).isSuccess shouldBe true
        driver.bothPass()

        // Delirium -> two +1/+1 counters; the now-4-power creature deals 4 to the 2/3, killing it.
        plusCounters(driver, yours) shouldBe 2
        driver.findPermanent(driver.player2, "Minotaur Warrior") shouldBe null
    }
})
