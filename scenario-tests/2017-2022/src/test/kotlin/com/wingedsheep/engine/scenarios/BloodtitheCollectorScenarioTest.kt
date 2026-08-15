package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bloodtithe Collector (MID #90, reprinted in FDN #751) — {4}{B} 3/4 flyer whose ETB reads
 * "if an opponent lost life this turn, each opponent discards a card."
 *
 * Proves the intervening "if" (CR 603.4): the discard only happens when an opponent has already
 * lost life this turn. A Lightning Bolt to the opponent's face satisfies the condition; without it,
 * the trigger does nothing.
 */
class BloodtitheCollectorScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 30))
        return d
    }

    /** Resolve the stack, answering any forced discard the opponent is handed along the way. */
    fun GameTestDriver.settle(maxSteps: Int = 20) {
        var guard = 0
        while ((stackSize > 0 || state.pendingDecision != null) && guard++ < maxSteps) {
            when (val pending = state.pendingDecision) {
                is SelectCardsDecision -> submitCardSelection(pending.playerId, pending.options.take(1))
                else -> bothPass()
            }
        }
    }

    test("each opponent discards when an opponent lost life this turn") {
        val d = driver()
        val you = d.player1
        val opp = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Bolt the opponent so they lose life this turn (does not touch their hand).
        val bolt = d.putCardInHand(you, "Lightning Bolt")
        d.giveMana(you, Color.RED, 1)
        d.castSpell(you, bolt, listOf(opp)).isSuccess shouldBe true
        d.bothPass()

        // Cast Bloodtithe Collector ({4}{B}); its ETB sees the life loss → opponent discards one.
        val handBefore = d.getHandSize(opp)
        val collector = d.putCardInHand(you, "Bloodtithe Collector")
        d.giveMana(you, Color.BLACK, 5)
        d.castSpell(you, collector).isSuccess shouldBe true
        d.settle()

        d.getHandSize(opp) shouldBe handBefore - 1
    }

    test("no discard when no opponent has lost life this turn") {
        val d = driver()
        val you = d.player1
        val opp = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val handBefore = d.getHandSize(opp)
        val collector = d.putCardInHand(you, "Bloodtithe Collector")
        d.giveMana(you, Color.BLACK, 5)
        d.castSpell(you, collector).isSuccess shouldBe true
        d.settle()

        d.getHandSize(opp) shouldBe handBefore
    }
})
