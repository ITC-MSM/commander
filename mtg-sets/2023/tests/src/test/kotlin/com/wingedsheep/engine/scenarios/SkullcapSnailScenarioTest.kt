package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SkullcapSnail
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Skullcap Snail (LCI #119) — {1}{B} Creature — Fungus Snail 1/1.
 *
 * "When this creature enters, target opponent exiles a card from their hand."
 *
 * The target opponent chooses which card of their own hand to exile
 * (`Patterns.Hand.exileFromHand` derives the chooser from the effect target).
 */
class SkullcapSnailScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SkullcapSnail)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("ETB makes the target opponent exile a card of their choice from their hand") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Give the opponent a distinctive card to exile so we can assert deterministically.
        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        val oppHandBefore = driver.getHand(opp).size

        // Cast the snail.
        val snail = driver.putCardInHand(me, "Skullcap Snail")
        driver.giveMana(me, Color.BLACK, 2)
        driver.castSpell(me, snail)

        // Resolve the creature spell and its ETB trigger, answering decisions as they arise.
        var guard = 0
        while (guard++ < 30) {
            when (val pd = driver.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(pd.playerId, listOf(opp))
                is SelectCardsDecision -> driver.submitCardSelection(pd.playerId, listOf(bolt))
                else -> if (driver.state.stack.isNotEmpty()) driver.bothPass() else break
            }
        }

        // The snail is in play.
        driver.findPermanent(me, "Skullcap Snail") shouldNotBe null
        // The opponent's chosen card was exiled from their hand.
        driver.getExileCardNames(opp).contains("Lightning Bolt") shouldBe true
        driver.getHand(opp).size shouldBe oppHandBefore - 1
    }
})
