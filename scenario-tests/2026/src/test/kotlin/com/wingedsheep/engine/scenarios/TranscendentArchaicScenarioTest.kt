package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.TranscendentArchaic
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Transcendent Archaic ({7}, 6/6 Avatar, Vigilance):
 *   "Converge — When this creature enters, you may draw X cards, where X is the number of colors of
 *    mana spent to cast this spell. If you draw one or more cards this way, discard two cards."
 *
 * Pins the Converge-driven ETB looter:
 *  - X = `DynamicAmount.DistinctColorsManaSpent`, read off the entering creature's recorded payment.
 *  - The draw is optional (a `MayEffect` yes/no). Declining draws and discards nothing.
 *  - The discard is gated on "you drew one or more this way" — i.e. X >= 1. A `yes` with X = 0
 *    (all-colourless payment) draws nothing and must NOT force the discard.
 */
class TranscendentArchaicScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(TranscendentArchaic))
        return driver
    }

    fun startTurn(driver: GameTestDriver): GameTestDriver {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Resolve the creature + its ETB trigger until the "may draw X" YesNo prompt surfaces. */
    fun resolveUntilMayPrompt(driver: GameTestDriver) {
        var guard = 0
        while (driver.pendingDecision == null && (driver.stackSize > 0 || guard == 0) && guard < 8) {
            driver.bothPass()
            guard++
        }
    }

    test("two colors spent, accept the draw → draw 2 then discard 2 (net hand unchanged)") {
        val driver = createDriver()
        startTurn(driver)
        val p = driver.activePlayer!!
        val spell = driver.putCardInHand(p, "Transcendent Archaic")
        // {7} paid with mana of two distinct colors → X = 2.
        driver.giveMana(p, Color.RED, 4)
        driver.giveMana(p, Color.BLUE, 3)

        val handBeforeCast = driver.getHandSize(p)
        driver.castSpell(p, spell).isSuccess shouldBe true
        resolveUntilMayPrompt(driver)

        // The "may draw X" prompt.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p, true)

        // Drew X = 2 → must now discard exactly two.
        val handAfterDraw = driver.getHandSize(p)
        handAfterDraw shouldBe (handBeforeCast - 1 /* cast the creature */ + 2 /* drew 2 */)

        val discard = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        discard.minSelections shouldBe 2
        discard.maxSelections shouldBe 2
        driver.submitCardSelection(p, driver.getHand(p).take(2))

        // Net: -1 (creature) +2 (draw) -2 (discard) = -1 relative to before-cast hand.
        driver.getHandSize(p) shouldBe (handBeforeCast - 1)
    }

    test("all colorless cast, accept the draw → X = 0, draw nothing, no discard") {
        val driver = createDriver()
        startTurn(driver)
        val p = driver.activePlayer!!
        val spell = driver.putCardInHand(p, "Transcendent Archaic")
        driver.giveColorlessMana(p, 7) // 0 colors → X = 0

        val handBeforeCast = driver.getHandSize(p)
        driver.castSpell(p, spell).isSuccess shouldBe true
        resolveUntilMayPrompt(driver)

        // Still get the optional "draw X" prompt (X just happens to be 0).
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p, true)

        // Drew zero → "draw one or more this way" is false → no discard prompt.
        (driver.pendingDecision is SelectCardsDecision) shouldBe false
        driver.getHandSize(p) shouldBe (handBeforeCast - 1) // only the cast creature left hand
    }

    test("decline the draw → no draw, no discard even with colors spent") {
        val driver = createDriver()
        startTurn(driver)
        val p = driver.activePlayer!!
        val spell = driver.putCardInHand(p, "Transcendent Archaic")
        driver.giveMana(p, Color.WHITE, 3)
        driver.giveMana(p, Color.GREEN, 4) // X would be 2 if drawn

        val handBeforeCast = driver.getHandSize(p)
        driver.castSpell(p, spell).isSuccess shouldBe true
        resolveUntilMayPrompt(driver)

        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(p, false) // decline

        (driver.pendingDecision is SelectCardsDecision) shouldBe false
        driver.getHandSize(p) shouldBe (handBeforeCast - 1)
    }
})
