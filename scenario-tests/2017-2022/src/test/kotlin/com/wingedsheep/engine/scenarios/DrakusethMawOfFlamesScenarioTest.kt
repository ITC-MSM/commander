package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m20.cards.DrakusethMawOfFlames
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Drakuseth, Maw of Flames (M20 #136) — {4}{R}{R}{R} Legendary Dragon 7/7, Flying.
 *
 *   "Whenever Drakuseth attacks, it deals 4 damage to any target and 3 damage to each of up to
 *    two other targets."
 *
 * Verifies the split damage: the mandatory "any target" (index 0) hits the defending player for 4,
 * and the "up to two other targets" pair (index 1) each take 3 — enough to kill two 2/2s.
 */
class DrakusethMawOfFlamesScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + DrakusethMawOfFlames)
        initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
    }

    test("deals 4 to the defending player and 3 to each of two other creatures") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        val drakuseth = d.putCreatureOnBattlefield(me, "Drakuseth, Maw of Flames")
        d.removeSummoningSickness(drakuseth)
        // Two 2/2 blockers on the opponent's side — each dies to 3 damage.
        val bear1 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val bear2 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(drakuseth), opp).error shouldBe null

        // The attack trigger goes on the stack and pauses to choose targets:
        // requirement 0 = "any target" (hit the opponent), requirement 1 = "up to two other
        // targets" (the two bears).
        while (d.pendingDecision == null && d.state.stack.isNotEmpty()) d.bothPass()
        (d.pendingDecision as ChooseTargetsDecision)
        d.submitMultiTargetSelection(
            me,
            mapOf(0 to listOf(opp), 1 to listOf(bear1, bear2))
        ).error shouldBe null

        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getLifeTotal(opp) shouldBe 16
        d.getGraveyard(opp) shouldContain bear1
        d.getGraveyard(opp) shouldContain bear2
    }
})
