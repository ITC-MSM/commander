package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.PerforatingArtist
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Perforating Artist (FDN) — {1}{B}{R} Creature — Devil 3/2.
 *
 * Deathtouch. Raid — At the beginning of your end step, if you attacked this turn, each opponent
 * loses 3 life unless that player sacrifices a nonland permanent of their choice or discards a card.
 *
 * Exercises the raid intervening-if gate (no attack → no trigger), and the per-opponent punisher:
 * the opponent may discard to avoid the loss, or take 3 life loss.
 */
class PerforatingArtistScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PerforatingArtist)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.advanceToMyEndStep() {
        var guard = 0
        while (currentStep != Step.END && guard++ < 60) {
            if (state.stack.isNotEmpty() || pendingDecision != null) bothPass() else passPriorityUntil(Step.END)
        }
    }

    test("Raid gate: without attacking, the end-step trigger does nothing") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        driver.putCreatureOnBattlefield(me, "Perforating Artist")
        val oppLifeBefore = driver.getLifeTotal(opp)

        driver.advanceToMyEndStep()

        // No attack this turn → intervening-if fails → no decision, no life loss.
        driver.pendingDecision shouldBe null
        driver.getLifeTotal(opp) shouldBe oppLifeBefore
    }

    test("after attacking, an opponent who discards avoids losing life") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        val artist = driver.putCreatureOnBattlefield(me, "Perforating Artist")
        driver.removeSummoningSickness(artist)
        // Opponent has no nonland permanents; give a card so discard is the sole avoidance option.
        driver.putCardInHand(opp, "Lightning Bolt")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(artist), opp)
        driver.advanceToMyEndStep()

        var lifeAtDecision = -1
        var discarded = false
        var guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseOptionDecision -> {
                    if (lifeAtDecision < 0) lifeAtDecision = driver.getLifeTotal(opp)
                    val discardIdx = pd.options.indexOfFirst { it.contains("Discard", ignoreCase = true) }
                    driver.submitDecision(opp, OptionChosenResponse(pd.id, discardIdx))
                }
                is SelectCardsDecision -> {
                    driver.submitCardSelection(opp, pd.options.take(1))
                    discarded = true
                }
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }

        discarded shouldBe true
        // Discarded to pay → no life lost to the punisher.
        driver.getLifeTotal(opp) shouldBe lifeAtDecision
    }

    test("after attacking, an opponent who declines loses 3 life") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        val artist = driver.putCreatureOnBattlefield(me, "Perforating Artist")
        driver.removeSummoningSickness(artist)
        driver.putCardInHand(opp, "Lightning Bolt")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(artist), opp)
        driver.advanceToMyEndStep()

        var lifeAtDecision = -1
        var guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseOptionDecision -> {
                    if (lifeAtDecision < 0) lifeAtDecision = driver.getLifeTotal(opp)
                    // The suffer option ("loses 3 life") is always appended last.
                    driver.submitDecision(opp, OptionChosenResponse(pd.id, pd.options.lastIndex))
                }
                is SelectCardsDecision -> driver.submitCardSelection(opp, pd.options.take(1))
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }

        // Declined the cost → lost 3 life to the punisher.
        driver.getLifeTotal(opp) shouldBe lifeAtDecision - 3
    }
})
