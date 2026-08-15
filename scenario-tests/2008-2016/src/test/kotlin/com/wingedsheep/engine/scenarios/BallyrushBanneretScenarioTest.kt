package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ballyrush Banneret (MOR #1, reprinted in FDN #567) — {1}{W} Kithkin Soldier, 2/1.
 *
 *   "Kithkin spells and Soldier spells you cast cost {1} less to cast."
 *
 * Verifies the tribal spell-cost reduction: a Soldier creature spell can be cast for
 * one mana less than its printed cost, while an unrelated (non-Kithkin, non-Soldier)
 * spell is unaffected.
 */
class BallyrushBanneretScenarioTest : FunSpec({

    // {2}{W} Soldier creature — qualifies for the reduction.
    val testSoldier = card("Test Soldier") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Human Soldier"
        power = 2
        toughness = 2
    }

    // {2}{W} vanilla creature with no matching subtype — should NOT be reduced.
    val testBear = card("Test Bear") {
        manaCost = "{2}{W}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(testSoldier)
        driver.registerCard(testBear)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("a Soldier spell costs {1} less with Ballyrush Banneret out") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putCreatureOnBattlefield(player, "Ballyrush Banneret")
        val soldier = driver.putCardInHand(player, "Test Soldier")

        // Only 2 mana — enough for the reduced {1}{W}, but not the printed {2}{W}.
        driver.giveMana(player, Color.WHITE, 2)

        val result = driver.submit(
            CastSpell(playerId = player, cardId = soldier, paymentStrategy = PaymentStrategy.AutoPay)
        )
        result.isSuccess shouldBe true

        driver.bothPass() // resolve the creature spell
        driver.getCreatures(player).contains(soldier) shouldBe true
    }

    test("a non-Kithkin, non-Soldier spell is unaffected by the reduction") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putCreatureOnBattlefield(player, "Ballyrush Banneret")
        val bear = driver.putCardInHand(player, "Test Bear")

        // Same 2 mana — not enough for the un-reduced {2}{W}.
        driver.giveMana(player, Color.WHITE, 2)

        val result = driver.submit(
            CastSpell(playerId = player, cardId = bear, paymentStrategy = PaymentStrategy.AutoPay)
        )
        result.isSuccess shouldBe false
    }
})
