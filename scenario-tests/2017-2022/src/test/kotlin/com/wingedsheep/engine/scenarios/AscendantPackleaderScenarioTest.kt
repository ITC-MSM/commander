package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ascendant Packleader (VOW #186) — {G} Creature — Wolf, 2/1.
 *
 * "This creature enters with a +1/+1 counter on it if you control a permanent with mana value 4
 *  or greater.
 *  Whenever you cast a spell with mana value 4 or greater, put a +1/+1 counter on this creature."
 *
 * Two independent mana-value-gated mechanics:
 *   - a conditional [EntersWithCounters] replacement gated on the intervening condition "you control
 *     a permanent with mana value 4 or greater" (evaluated as it enters, CR 614), and
 *   - a [Triggers.youCastSpell] filtered to mana value >= 4 that adds a +1/+1 counter.
 *
 * Force of Nature ({3}{G}{G}, mana value 5) serves as both the qualifying permanent and the
 * qualifying spell; Grizzly Bears ({1}{G}, mana value 2) is the sub-threshold control.
 * Counters are read through projected power/toughness (a +1/+1 counter turns the 2/1 into a 3/2).
 */
class AscendantPackleaderScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0
    fun GameTestDriver.toughness(id: EntityId): Int = state.projectedState.getToughness(id) ?: 0

    test("enters with a +1/+1 counter when you control a mana value 4+ permanent") {
        val driver = newDriver()
        val player = driver.player1

        // Force of Nature is a mana value 5 permanent already on the battlefield.
        driver.putCreatureOnBattlefield(player, "Force of Nature")

        val packleader = driver.putCardInHand(player, "Ascendant Packleader")
        driver.giveMana(player, Color.GREEN, 1)
        driver.castSpell(player, packleader).isSuccess shouldBe true
        driver.bothPass() // resolve → enters the battlefield

        // 2/1 base + a +1/+1 counter = 3/2.
        driver.power(packleader) shouldBe 3
        driver.toughness(packleader) shouldBe 2
    }

    test("enters as a vanilla 2/1 when you control no mana value 4+ permanent") {
        val driver = newDriver()
        val player = driver.player1

        // Only a mana value 2 permanent — below the threshold.
        driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        val packleader = driver.putCardInHand(player, "Ascendant Packleader")
        driver.giveMana(player, Color.GREEN, 1)
        driver.castSpell(player, packleader).isSuccess shouldBe true
        driver.bothPass()

        driver.power(packleader) shouldBe 2
        driver.toughness(packleader) shouldBe 1
    }

    test("casting a mana value 4+ spell puts a +1/+1 counter on the Packleader") {
        val driver = newDriver()
        val player = driver.player1

        // Packleader already on the battlefield; nothing else, so it entered vanilla 2/1.
        val packleader = driver.putCreatureOnBattlefield(player, "Ascendant Packleader")
        driver.power(packleader) shouldBe 2

        // Cast Force of Nature (mana value 5) — the cast trigger fires.
        val force = driver.putCardInHand(player, "Force of Nature")
        driver.giveMana(player, Color.GREEN, 5)
        driver.castSpell(player, force).isSuccess shouldBe true
        driver.bothPass() // resolve the trigger (and the spell)

        driver.power(packleader) shouldBe 3
        driver.toughness(packleader) shouldBe 2
    }

    test("casting a mana value 3-or-less spell does not trigger the counter") {
        val driver = newDriver()
        val player = driver.player1

        val packleader = driver.putCreatureOnBattlefield(player, "Ascendant Packleader")

        // Grizzly Bears is mana value 2 — below the threshold, so no trigger.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.giveMana(player, Color.GREEN, 2)
        driver.castSpell(player, bears).isSuccess shouldBe true
        driver.bothPass()

        driver.power(packleader) shouldBe 2
        driver.toughness(packleader) shouldBe 1
    }
})
