package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Eshki Dragonclaw ({1}{G}{U}{R}, 4/4, Vigilance/Trample/Ward {1}):
 * "At the beginning of combat on your turn, if you've cast both a creature spell and a
 *  noncreature spell this turn, draw a card and put two +1/+1 counters on Eshki Dragonclaw."
 *
 * The intervening "if" is two `Conditions.YouCastSpellsThisTurn` (one per spell-kind filter)
 * combined with `Conditions.All`.
 */
class EshkiDragonclawTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("having cast both a creature and noncreature spell, combat trigger draws and adds two counters") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val eshki = driver.putCreatureOnBattlefield(me, "Eshki Dragonclaw")

        // Cast a creature spell ...
        driver.giveMana(me, Color.GREEN, 2)
        val bears = driver.putCardInHand(me, "Grizzly Bears")
        driver.castSpell(me, bears).isSuccess shouldBe true
        driver.bothPass()

        // ... and a noncreature spell.
        driver.giveMana(me, Color.RED, 1)
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.castSpell(me, bolt, listOf(opp)).isSuccess shouldBe true
        driver.bothPass()

        val handBefore = driver.state.getZone(ZoneKey(me, Zone.HAND)).size

        // Advance to beginning of combat — the trigger fires and resolves.
        driver.passPriorityUntil(Step.BEGIN_COMBAT, maxPasses = 200)
        driver.bothPass()

        plusCounters(driver, eshki) shouldBe 2
        driver.state.getZone(ZoneKey(me, Zone.HAND)).size shouldBe handBefore + 1
    }

    test("casting only a creature spell does not satisfy the intervening if (no draw, no counters)") {
        val driver = createDriver()
        val me = driver.activePlayer!!

        val eshki = driver.putCreatureOnBattlefield(me, "Eshki Dragonclaw")

        driver.giveMana(me, Color.GREEN, 2)
        val bears = driver.putCardInHand(me, "Grizzly Bears")
        driver.castSpell(me, bears).isSuccess shouldBe true
        driver.bothPass()

        val handBefore = driver.state.getZone(ZoneKey(me, Zone.HAND)).size

        driver.passPriorityUntil(Step.BEGIN_COMBAT, maxPasses = 200)
        driver.bothPass()

        plusCounters(driver, eshki) shouldBe 0
        driver.state.getZone(ZoneKey(me, Zone.HAND)).size shouldBe handBefore
    }
})
