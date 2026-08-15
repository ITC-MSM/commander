package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.FlowState
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Flow State {1}{U} — Sorcery.
 * "Look at the top three cards of your library. Put one of them into your hand and the rest on
 *  the bottom of your library in any order. If there is an instant card and a sorcery card in
 *  your graveyard, instead put two of them into your hand and the rest on the bottom of your
 *  library in any order."
 *
 * Exercises the conditional keep-count: without both an instant and a sorcery in the graveyard,
 * one card is kept; with both, two are kept (the `DynamicAmount.Conditional` over the new
 * `Conditions.GraveyardContains` check).
 */
class FlowStateTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FlowState))
        return driver
    }

    fun resolveFlowState(driver: GameTestDriver, me: com.wingedsheep.sdk.model.EntityId): Int {
        driver.bothPass()
        // First decision: choose the card(s) to keep.
        driver.isPaused shouldBe true
        val select = driver.pendingDecision as SelectCardsDecision
        val keepCount = select.maxSelections
        driver.submitCardSelection(me, select.options.take(keepCount))
        // Optional: order the rest onto the bottom of the library.
        if (driver.isPaused && driver.pendingDecision is ReorderLibraryDecision) {
            val reorder = driver.pendingDecision as ReorderLibraryDecision
            driver.submitOrderedResponse(me, reorder.cards)
        }
        return keepCount
    }

    test("Without an instant and a sorcery in graveyard: keep one") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spell = driver.putCardInHand(me, "Flow State")
        repeat(2) { driver.putLandOnBattlefield(me, "Island") }
        val handBefore = driver.getHandSize(me) - 1

        driver.castSpell(me, spell)
        val kept = resolveFlowState(driver, me)

        kept shouldBe 1
        driver.getHandSize(me) shouldBe handBefore + 1
    }

    test("With an instant and a sorcery in graveyard: keep two") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Seed the graveyard with an instant and a sorcery card.
        driver.putCardInGraveyard(me, "Lightning Bolt") // instant
        driver.putCardInGraveyard(me, "Careful Study")  // sorcery

        val spell = driver.putCardInHand(me, "Flow State")
        repeat(2) { driver.putLandOnBattlefield(me, "Island") }
        val handBefore = driver.getHandSize(me) - 1

        driver.castSpell(me, spell)
        val kept = resolveFlowState(driver, me)

        kept shouldBe 2
        driver.getHandSize(me) shouldBe handBefore + 2
    }
})
