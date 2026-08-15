package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.VengefulBloodwitch
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Vengeful Bloodwitch {1}{B} — Creature — Vampire Warlock 1/1
 *   Whenever this creature or another creature you control dies, target opponent loses 1 life
 *   and you gain 1 life.
 *
 * Proves the Blood Artist shape via [com.wingedsheep.sdk.dsl.Triggers.YourCreatureDies]:
 *  - another creature you control dying drains the chosen opponent,
 *  - the witch's OWN death also drains (the "this creature or" clause).
 */
class VengefulBloodwitchScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(VengefulBloodwitch))
        return driver
    }

    fun drainStack(driver: GameTestDriver, opponentToTarget: EntityId) {
        var safety = 0
        while (driver.stackSize > 0 && safety < 30) {
            val pending = driver.state.pendingDecision
            if (pending != null) {
                driver.submitTargetSelection(pending.playerId, listOf(opponentToTarget))
            } else {
                driver.bothPass()
            }
            safety++
        }
    }

    test("another creature you control dying drains a target opponent") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youLifeBefore = driver.getLifeTotal(you)
        val oppLifeBefore = driver.getLifeTotal(opponent)

        driver.putCreatureOnBattlefield(you, "Vengeful Bloodwitch")
        val frail = driver.putCreatureOnBattlefield(you, "Goblin Guide") // 2/1

        driver.giveMana(opponent, Color.RED, 1)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.passPriority(you)
        driver.castSpellWithTargets(opponent, bolt, listOf(ChosenTarget.Permanent(frail))).error shouldBe null

        drainStack(driver, opponent)

        driver.getLifeTotal(opponent) shouldBe (oppLifeBefore - 1)
        driver.getLifeTotal(you) shouldBe (youLifeBefore + 1)
    }

    test("the witch's own death also drains") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        val youLifeBefore = driver.getLifeTotal(you)
        val oppLifeBefore = driver.getLifeTotal(opponent)

        val witch = driver.putCreatureOnBattlefield(you, "Vengeful Bloodwitch")

        driver.giveMana(opponent, Color.RED, 1)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.passPriority(you)
        driver.castSpellWithTargets(opponent, bolt, listOf(ChosenTarget.Permanent(witch))).error shouldBe null

        drainStack(driver, opponent)

        driver.getLifeTotal(opponent) shouldBe (oppLifeBefore - 1)
        driver.getLifeTotal(you) shouldBe (youLifeBefore + 1)
    }
})
