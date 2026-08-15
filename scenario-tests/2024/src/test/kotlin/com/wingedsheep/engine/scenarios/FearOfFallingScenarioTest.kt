package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfFalling
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fear of Falling (DSK #56) — "Flying. Whenever this creature attacks, target creature defending
 * player controls gets -2/-0 and loses flying until your next turn."
 *
 * Exercises the attack trigger composing `ModifyStatsEffect(-2, 0, UntilYourNextTurn)` +
 * `RemoveKeyword(FLYING, UntilYourNextTurn)` on a chosen creature the defending player controls.
 */
class FearOfFallingScenarioTest : FunSpec({

    // A 3/3 flyer for the defending player to be targeted by the attack trigger.
    val FlyingBear = CardDefinition(
        name = "Test Flying Bear",
        manaCost = ManaCost.parse("{2}{G}"),
        typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
        creatureStats = CreatureStats(3, 3),
        keywords = setOf(Keyword.FLYING),
    )

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(FearOfFalling, FlyingBear))
        return d
    }

    test("attack trigger gives the targeted defending creature -2/-0 and removes flying") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightmare = driver.putCreatureOnBattlefield(p1, "Fear of Falling")
        driver.removeSummoningSickness(nightmare)
        val victim = driver.putCreatureOnBattlefield(p2, "Test Flying Bear")

        // Baseline before the attack.
        run {
            val projected = StateProjector().project(driver.state)
            projected.getPower(victim) shouldBe 3
            projected.hasKeyword(victim, Keyword.FLYING) shouldBe true
        }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(nightmare), p2).error shouldBe null
        // Attack trigger goes on the stack and pauses for its target (a creature the
        // defending player controls).
        require(driver.pendingDecision is ChooseTargetsDecision) {
            "Expected a target decision for the attack trigger, got ${driver.pendingDecision}"
        }
        driver.submitTargetSelection(p1, listOf(victim)).error shouldBe null
        driver.bothPass() // resolve the attack trigger

        val projected = StateProjector().project(driver.state)
        projected.getPower(victim) shouldBe 1 // 3 - 2
        projected.hasKeyword(victim, Keyword.FLYING) shouldBe false
    }
})
