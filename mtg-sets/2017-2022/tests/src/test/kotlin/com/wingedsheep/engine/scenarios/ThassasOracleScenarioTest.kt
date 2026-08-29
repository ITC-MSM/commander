package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.thb.cards.ThassasOracle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Thassa's Oracle (THB #73) — {U}{U} Creature — Merfolk Wizard 1/3.
 *
 *   "When this creature enters, look at the top X cards of your library, where X is your devotion
 *    to blue. Put up to one of them on top of your library and the rest on the bottom of your
 *    library in a random order. If X is greater than or equal to the number of cards in your
 *    library, you win the game."
 *
 * X is read twice in one resolution, so both reads have to see the Oracle itself on the
 * battlefield (its own {U}{U} is two devotion — the 2020-01-24 ruling). The win check runs after
 * the looked-at cards are put back, which is what makes "the number of cards in your library"
 * count them.
 */
class ThassasOracleScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + ThassasOracle)
        initMirrorMatch(deck = Deck.of("Island" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.trimLibrary(playerId: com.wingedsheep.sdk.model.EntityId, size: Int) {
        val keep = state.getLibrary(playerId).take(size)
        replaceState(state.copy(zones = state.zones + (ZoneKey(playerId, Zone.LIBRARY) to keep)))
    }

    test("looks at cards equal to devotion to blue and does not win with a full library") {
        val d = driver()
        val me = d.activePlayer!!

        val oracle = d.putCardInHand(me, "Thassa's Oracle")
        d.giveMana(me, Color.BLUE, 2)
        d.castSpell(me, oracle).error shouldBe null
        // Resolve the creature spell, then its enters trigger, stopping at the look decision.
        while (!d.isPaused && d.stackSize > 0) d.bothPass()

        val libraryBefore = d.state.getLibrary(me).size

        // Devotion to blue is 2 — the Oracle's own {U}{U}, counted because it is on the
        // battlefield as the trigger resolves.
        val look = d.pendingDecision as SelectCardsDecision
        look.options.size shouldBe 2
        look.minSelections shouldBe 0
        look.maxSelections shouldBe 1

        d.submitCardSelection(me, listOf(look.options.first()))
        while (d.isPaused) d.autoResolveDecision()

        // Every looked-at card went back — one on top, one on the bottom.
        d.state.getLibrary(me).size shouldBe libraryBefore
        d.state.gameOver shouldBe false
    }

    test("wins the game when devotion to blue is at least the number of cards in your library") {
        val d = driver()
        val me = d.activePlayer!!

        // Two cards left: devotion 2 >= library 2 once the looked-at cards are put back.
        d.trimLibrary(me, 2)

        val oracle = d.putCardInHand(me, "Thassa's Oracle")
        d.giveMana(me, Color.BLUE, 2)
        d.castSpell(me, oracle).error shouldBe null
        // Resolve the creature spell, then its enters trigger, stopping at the look decision.
        while (!d.isPaused && d.stackSize > 0) d.bothPass()

        val look = d.pendingDecision as SelectCardsDecision
        look.options.size shouldBe 2
        d.submitCardSelection(me, listOf(look.options.first()))
        while (d.isPaused) d.autoResolveDecision()

        d.assertGameOver(expectedWinner = me)
    }

    test("wins on an empty library without looking at anything") {
        val d = driver()
        val me = d.activePlayer!!

        d.trimLibrary(me, 0)

        val oracle = d.putCardInHand(me, "Thassa's Oracle")
        d.giveMana(me, Color.BLUE, 2)
        d.castSpell(me, oracle).error shouldBe null
        // Resolve the creature spell, then its enters trigger, stopping at the look decision.
        while (!d.isPaused && d.stackSize > 0) d.bothPass()

        // Nothing to look at, so no decision is raised at all — 2 >= 0 wins outright.
        d.assertGameOver(expectedWinner = me)
    }
})
