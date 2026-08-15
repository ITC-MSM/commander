package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dka.cards.PredatorOoze
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Predator Ooze.
 *
 * Predator Ooze: {G}{G}{G}
 * Creature — Ooze
 * 1/1
 * Indestructible
 * Whenever this creature attacks, put a +1/+1 counter on it.
 * Whenever a creature dealt damage by this creature this turn dies, put a +1/+1 counter on this creature.
 */
class PredatorOozeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PredatorOoze)
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("attacking puts a +1/+1 counter on Predator Ooze") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val ooze = driver.putCreatureOnBattlefield(attacker, "Predator Ooze")
        driver.removeSummoningSickness(ooze)

        driver.plusOneCounters(ooze) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(ooze), defender)

        // Attack trigger goes on the stack; resolve it.
        driver.bothPass()

        driver.plusOneCounters(ooze) shouldBe 1
    }

    test("a creature dealt combat damage by Predator Ooze that dies grows the Ooze; the Ooze survives via indestructible") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val ooze = driver.putCreatureOnBattlefield(attacker, "Predator Ooze")
        driver.removeSummoningSickness(ooze)

        // 2/2 blocker — dies to the post-attack 2/2 Ooze, and deals 2 back (lethal to a 1/1,
        // but the Ooze is indestructible so it survives).
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(ooze), defender)

        // Attack trigger resolves on the way to declare-blockers: Ooze becomes 2/2 (one counter).
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.plusOneCounters(ooze) shouldBe 1

        driver.declareBlockers(defender, mapOf(blocker to listOf(ooze)))

        // Combat damage: Ooze deals 2 to Grizzly Bears (dies); Bears deals 2 to the indestructible Ooze.
        // The dies-by-damage trigger then resolves on the way to end of combat.
        driver.passPriorityUntil(Step.END_COMBAT)

        // Blocker died.
        driver.findPermanent(defender, "Grizzly Bears") shouldBe null
        // Ooze survived the lethal damage (indestructible) and gained a second counter from the kill.
        driver.findPermanent(attacker, "Predator Ooze") shouldNotBe null
        driver.plusOneCounters(ooze) shouldBe 2
    }

    test("a creature dying to a different source (not the Ooze) does not trigger the Ooze") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        // The Ooze sits on the battlefield but does not attack — it deals no damage this turn.
        val ooze = driver.putCreatureOnBattlefield(attacker, "Predator Ooze")
        driver.removeSummoningSickness(ooze)

        // A separate attacker that trades with the defender's blocker; the Ooze is uninvolved.
        val bear = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(bear)
        val blocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        driver.plusOneCounters(ooze) shouldBe 0

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(bear), defender)
        driver.bothPass()

        driver.declareBlockers(defender, mapOf(blocker to listOf(bear)))
        driver.bothPass()

        // Both 2/2 bears die to each other; the Ooze dealt none of that damage.
        driver.passPriorityUntil(Step.END_COMBAT)
        driver.bothPass()

        driver.findPermanent(defender, "Grizzly Bears") shouldBe null
        // The Ooze never dealt damage to the dying creature, so it gains no counter.
        driver.plusOneCounters(ooze) shouldBe 0
    }
})
