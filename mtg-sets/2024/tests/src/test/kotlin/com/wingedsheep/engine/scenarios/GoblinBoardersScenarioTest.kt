package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.GoblinBoarders
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Goblin Boarders {2}{R} — Creature — Goblin Pirate 3/2
 *   Raid — This creature enters with a +1/+1 counter on it if you attacked this turn.
 *
 * "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger:
 * nothing goes on the stack, and with Raid satisfied the creature is a 4/3 from the
 * moment it hits the battlefield.
 */
class GoblinBoardersScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GoblinBoarders))
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("raid active: enters as a 4/3 with the counter already on it, no trigger on the stack") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // Attack this turn to satisfy Raid.
        val goblin = driver.putCreatureOnBattlefield(you, "Goblin Guide")
        driver.removeSummoningSickness(goblin)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(goblin), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.giveMana(you, Color.RED, 3)
        val boarders = driver.putCardInHand(you, "Goblin Boarders")
        driver.castSpell(you, boarders).error shouldBe null
        driver.bothPass() // resolve onto the battlefield

        // Replacement effect: the counter is there immediately and nothing triggered.
        driver.stackSize shouldBe 0
        val perm = driver.findPermanent(you, "Goblin Boarders")!!
        driver.plusOneCounters(perm) shouldBe 1
        driver.state.projectedState.getPower(perm) shouldBe 4
        driver.state.projectedState.getToughness(perm) shouldBe 3
    }

    test("no attack this turn: enters as a plain 3/2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!

        driver.giveMana(you, Color.RED, 3)
        val boarders = driver.putCardInHand(you, "Goblin Boarders")
        driver.castSpell(you, boarders).error shouldBe null
        driver.bothPass() // resolve onto the battlefield

        driver.stackSize shouldBe 0
        val perm = driver.findPermanent(you, "Goblin Boarders")!!
        driver.plusOneCounters(perm) shouldBe 0
        driver.state.projectedState.getPower(perm) shouldBe 3
        driver.state.projectedState.getToughness(perm) shouldBe 2
    }
})
