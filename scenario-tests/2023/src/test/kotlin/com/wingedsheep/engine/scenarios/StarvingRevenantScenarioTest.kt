package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

/**
 * Starving Revenant (LCI #123) — {2}{B}{B} Creature — Spirit Horror 4/4.
 *
 * "When this creature enters, surveil 2. Then for each card you put on top of your library, you
 *  draw a card and you lose 3 life.
 *  Descend 8 — Whenever you draw a card, if there are eight or more permanent cards in your
 *  graveyard, target opponent loses 1 life and you gain 1 life."
 *
 * The ETB is composed from the surveil macro (which stores the kept-on-top cards under the "toTop"
 * pipeline collection) plus a draw and a life-loss whose amounts are driven by
 * `DistinctEntitiesInCollections("toTop")`. The descend trigger is a "whenever you draw" trigger
 * gated by an intervening-if on eight-or-more permanent cards in the graveyard.
 */
class StarvingRevenantScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Cast Starving Revenant from hand and resolve until the ETB surveil selection pauses. */
    fun GameTestDriver.castRevenant(you: EntityId) {
        val spell = putCardInHand(you, "Starving Revenant")
        giveMana(you, Color.BLACK, 4)
        castSpell(you, spell)
        var guard = 0
        while (guard++ < 50 && !(isPaused && pendingDecision is SelectCardsDecision)) {
            if (!isPaused && state.stack.isNotEmpty()) bothPass() else break
        }
    }

    /** Resolve the rest of the ETB: any top-order pause, then all triggers on the stack. */
    fun GameTestDriver.finishResolution(you: EntityId) {
        var guard = 0
        while (guard++ < 50) {
            val pd = pendingDecision
            when {
                isPaused && pd is ReorderLibraryDecision -> submitOrderedResponse(you, pd.cards)
                !isPaused && state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    test("keeping both surveiled cards on top draws two and loses six life") {
        val d = driver()
        val you = d.player1
        val opp = d.getOpponent(you)

        val bottom = d.putCardOnTopOfLibrary(you, "Swamp")
        val top = d.putCardOnTopOfLibrary(you, "Swamp")

        d.castRevenant(you)
        val select = d.pendingDecision as SelectCardsDecision
        select.options.toSet() shouldBe setOf(top, bottom)

        // Put nothing into the graveyard — keep both cards on top.
        d.submitCardSelection(you, emptyList())
        d.finishResolution(you)

        d.isPaused shouldBe false
        // Drew both kept cards; lost 3 life each (empty graveyard ⇒ descend does not fire).
        d.getHand(you) shouldContainAll listOf(top, bottom)
        d.getLifeTotal(you) shouldBe 14
        d.getLifeTotal(opp) shouldBe 20
    }

    test("putting both surveiled cards into the graveyard draws nothing and loses no life") {
        val d = driver()
        val you = d.player1
        val opp = d.getOpponent(you)

        val bottom = d.putCardOnTopOfLibrary(you, "Swamp")
        val top = d.putCardOnTopOfLibrary(you, "Swamp")

        d.castRevenant(you)
        d.submitCardSelection(you, listOf(top, bottom))
        d.finishResolution(you)

        d.isPaused shouldBe false
        // No card put on top ⇒ no draw, no life loss.
        d.getGraveyard(you) shouldContainAll listOf(top, bottom)
        d.getLifeTotal(you) shouldBe 20
        d.getLifeTotal(opp) shouldBe 20
    }

    test("descend 8 drains once per drawn card when eight permanent cards are in the graveyard") {
        val d = driver()
        val you = d.player1
        val opp = d.getOpponent(you)

        // Eight permanent cards (lands) already in the graveyard satisfies descend 8.
        repeat(8) { d.putCardInGraveyard(you, "Swamp") }
        d.putCardOnTopOfLibrary(you, "Swamp")
        d.putCardOnTopOfLibrary(you, "Swamp")

        d.castRevenant(you)
        d.submitCardSelection(you, emptyList()) // keep both on top ⇒ draw two
        d.finishResolution(you)

        d.isPaused shouldBe false
        // Two draws: lose 3 each (−6) and gain 1 each from descend (+2) ⇒ 20 − 6 + 2 = 16.
        d.getLifeTotal(you) shouldBe 16
        // Descend fires once per draw ⇒ opponent loses 2.
        d.getLifeTotal(opp) shouldBe 18
    }

    test("descend 8 does not fire when fewer than eight permanent cards are in the graveyard") {
        val d = driver()
        val you = d.player1
        val opp = d.getOpponent(you)

        // Only seven permanent cards in the graveyard — below the descend-8 threshold.
        repeat(7) { d.putCardInGraveyard(you, "Swamp") }
        d.putCardOnTopOfLibrary(you, "Swamp")
        d.putCardOnTopOfLibrary(you, "Swamp")

        d.castRevenant(you)
        d.submitCardSelection(you, emptyList()) // keep both on top ⇒ draw two
        d.finishResolution(you)

        d.isPaused shouldBe false
        // Two draws lose 6 life; descend never fires (7 < 8), so no life swing.
        d.getLifeTotal(you) shouldBe 14
        d.getLifeTotal(opp) shouldBe 20
    }
})
