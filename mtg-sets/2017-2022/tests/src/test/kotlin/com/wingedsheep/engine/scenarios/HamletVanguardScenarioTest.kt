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
 * Hamlet Vanguard (VOW #201) — {2}{G} Creature — Human Warrior, 1/1.
 *
 * "Ward {2}
 *  This creature enters with two +1/+1 counters on it for each other nontoken Human you control."
 *
 * Three things about the count are worth proving rather than assuming, because each is a place the
 * card could be silently off:
 *
 *  1. **The rate.** The printed "two … for each" is a `Multiply` over the tally, not a second count,
 *     so two other Humans is four counters and not two.
 *  2. **"Other".** The Vanguard is itself a Human, and the count is evaluated as it enters — so
 *     `AggregateBattlefield.excludeSelf` has to drop exactly it. A board with no other Humans must
 *     leave a plain 1/1.
 *  3. **"Nontoken".** A Human *token* pays nothing, which is the whole reason the filter carries
 *     `nontoken()`; Join the Dance supplies two of them.
 *
 * P/T is read through projected state — a +1/+1 counter turns the 1/1 into a 2/2 there, and the card
 * carries no other continuous effect, so the projected numbers are the counter count plus one.
 */
class HamletVanguardScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.power(id: EntityId): Int = state.projectedState.getPower(id) ?: 0
    fun GameTestDriver.toughness(id: EntityId): Int = state.projectedState.getToughness(id) ?: 0

    fun GameTestDriver.castVanguard(player: EntityId): EntityId {
        val vanguard = putCardInHand(player, "Hamlet Vanguard")
        giveMana(player, Color.GREEN, 3)
        castSpell(player, vanguard).isSuccess shouldBe true
        bothPass() // resolve → enters the battlefield
        return vanguard
    }

    test("enters as a plain 1/1 when you control no other Humans") {
        val driver = newDriver()
        val vanguard = driver.castVanguard(driver.player1)

        driver.power(vanguard) shouldBe 1
        driver.toughness(vanguard) shouldBe 1
    }

    test("two counters for each other nontoken Human — the numeral is a rate, not a count") {
        val driver = newDriver()
        val player = driver.player1

        driver.putCreatureOnBattlefield(player, "Apprentice Sharpshooter") // Human Archer
        driver.putCreatureOnBattlefield(player, "Daybreak Combatants") // Human Warrior

        val vanguard = driver.castVanguard(player)

        // Two other Humans × two counters each = four counters on a 1/1.
        driver.power(vanguard) shouldBe 5
        driver.toughness(vanguard) shouldBe 5
    }

    test("the Vanguard does not count itself, and a non-Human does not count either") {
        val driver = newDriver()
        val player = driver.player1

        driver.putCreatureOnBattlefield(player, "Grizzly Bears") // a Bear, not a Human
        driver.putCreatureOnBattlefield(player, "Apprentice Sharpshooter")

        val vanguard = driver.castVanguard(player)

        // Only the Sharpshooter qualifies: two counters, not four.
        driver.power(vanguard) shouldBe 3
        driver.toughness(vanguard) shouldBe 3
    }

    test("Humans an opponent controls are not counted") {
        val driver = newDriver()
        val player = driver.player1

        driver.putCreatureOnBattlefield(driver.player2, "Apprentice Sharpshooter")

        val vanguard = driver.castVanguard(player)

        driver.power(vanguard) shouldBe 1
        driver.toughness(vanguard) shouldBe 1
    }

    test("Human tokens pay nothing") {
        val driver = newDriver()
        val player = driver.player1

        // Join the Dance creates two 1/1 white Human creature *tokens*.
        val dance = driver.putCardInHand(player, "Join the Dance")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.WHITE, 1)
        driver.castSpell(player, dance).isSuccess shouldBe true
        driver.bothPass()

        val vanguard = driver.castVanguard(player)

        driver.power(vanguard) shouldBe 1
        driver.toughness(vanguard) shouldBe 1
    }
})
