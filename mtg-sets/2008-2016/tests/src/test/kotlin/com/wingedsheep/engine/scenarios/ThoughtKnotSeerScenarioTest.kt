package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.Terror
import com.wingedsheep.mtg.sets.definitions.ogw.cards.ThoughtKnotSeer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Thought-Knot Seer (OGW #9) — {3}{C} Creature — Eldrazi 4/4.
 *
 * "When this creature enters, target opponent reveals their hand. You choose a nonland card
 *  from it and exile that card.
 *  When this creature leaves the battlefield, target opponent draws a card."
 *
 * The ETB is `Patterns.Hand.revealHandAndExileChosen` (Cruelclaw's Heist / Skullcap Snail shape);
 * the LTB is a self `Triggers.LeavesBattlefield` (Goblin Firebug shape) targeting an opponent.
 */
class ThoughtKnotSeerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ThoughtKnotSeer)
        driver.registerCard(Terror)
        driver.initMirrorMatch(deck = Deck.of("Wastes" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("ETB: target opponent reveals hand, I choose and exile a nonland card") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        val oppHandBefore = driver.getHand(opp).size

        val seer = driver.putCardInHand(me, "Thought-Knot Seer")
        repeat(4) { driver.putLandOnBattlefield(me, "Wastes") } // {3}{C}
        driver.castSpell(me, seer)

        var guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(pd.playerId, listOf(opp))
                is SelectCardsDecision -> driver.submitCardSelection(pd.playerId, listOf(bolt))
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }

        driver.findPermanent(me, "Thought-Knot Seer") shouldNotBe null
        driver.getExileCardNames(opp).contains("Lightning Bolt") shouldBe true
        driver.getHand(opp).size shouldBe oppHandBefore - 1
    }

    test("LTB: target opponent draws a card when this leaves the battlefield") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        val bolt = driver.putCardInHand(opp, "Lightning Bolt")

        val seer = driver.putCardInHand(me, "Thought-Knot Seer")
        repeat(4) { driver.putLandOnBattlefield(me, "Wastes") } // {3}{C}
        driver.castSpell(me, seer)

        var guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(pd.playerId, listOf(opp))
                is SelectCardsDecision -> driver.submitCardSelection(pd.playerId, listOf(bolt))
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }
        val tksId = driver.findPermanent(me, "Thought-Knot Seer")
        tksId shouldNotBe null

        // Destroy it with Terror (nonartifact, nonblack — a legal target); the target opponent of
        // the seer's controller (me) — i.e. opp — should draw a card as it leaves.
        val terror = driver.putCardInHand(opp, "Terror")
        driver.giveMana(opp, Color.BLACK, 2)
        // Priority is with `me` once the ETB trigger loop above finishes — pass it to `opp`
        // before they can cast the removal spell.
        driver.passPriority(me)
        driver.castSpell(opp, terror, listOf(tksId!!)).error shouldBe null

        val oppHandBeforeLtb = driver.getHand(opp).size

        guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(pd.playerId, listOf(opp))
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }

        driver.findPermanent(me, "Thought-Knot Seer") shouldBe null
        driver.getHand(opp).size shouldBe (oppHandBeforeLtb + 1)
    }
})
