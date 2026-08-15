package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Demonic Pact (ORI #92, canonical printing) — {2}{B}{B} Enchantment.
 *
 * "At the beginning of your upkeep, choose one that hasn't been chosen —
 *  • This enchantment deals 4 damage to any target and you gain 4 life.
 *  • Target opponent discards two cards.
 *  • Draw two cards.
 *  • You lose the game."
 *
 * Proven here:
 *  - the upkeep trigger offers all four modes the first time, and the damage/lifegain mode resolves;
 *  - a chosen mode is never offered again ("that hasn't been chosen" — memory keyed to the permanent);
 *  - the trigger fires only on the controller's upkeep, not an opponent's;
 *  - after the three "good" modes are spent, only "You lose the game" remains — and taking it ends
 *    the game (the printed trap).
 */
class DemonicPactScenarioTest : ScenarioTestBase() {

    private val damageMode = "This enchantment deals 4 damage to any target and you gain 4 life"
    private val discardMode = "Target opponent discards two cards"
    private val drawMode = "Draw two cards"
    private val loseMode = "You lose the game"

    /** Resolve the stack until the modal ChooseOptionDecision appears. */
    private fun TestGame.resolveToModeChoice(): ChooseOptionDecision {
        resolveStack()
        val decision = getPendingDecision()
        decision.shouldNotBeNull()
        return decision as ChooseOptionDecision
    }

    private fun TestGame.chooseMode(decision: ChooseOptionDecision, description: String) {
        val index = decision.options.indexOf(description)
        check(index >= 0) { "Mode '$description' not offered; options=${decision.options}" }
        submitDecision(OptionChosenResponse(decision.id, index))
    }

    /**
     * Resolve the stack, answering any card-selection prompt the chosen mode produces (the discard
     * mode makes the *opponent* pick two cards). Stops on any other decision so the caller can
     * handle it explicitly.
     */
    private fun TestGame.resolveAnsweringCardSelections(maxIterations: Int = 20) {
        var guard = 0
        while (guard++ < maxIterations) {
            val decision = getPendingDecision()
            if (decision is SelectCardsDecision) {
                selectCards(decision.options.take(maxOf(decision.minSelections, 1)))
                continue
            }
            if (decision != null) return
            if (state.stack.isEmpty()) return
            resolveStack()
        }
    }

    /** Advance from the current point to the controller's next upkeep. */
    private fun TestGame.toNextOwnUpkeep() {
        passUntilPhase(Phase.ENDING, Step.END)
        passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // opponent's upkeep
        resolveStack()
        passUntilPhase(Phase.ENDING, Step.END)
        passUntilPhase(Phase.BEGINNING, Step.UPKEEP) // controller's upkeep again
    }

    init {
        test("first upkeep offers all four modes; the damage mode burns and gains life") {
            var builder = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Demonic Pact")
                .withLifeTotal(1, 20)
                .withLifeTotal(2, 20)
                .withActivePlayer(1)
                .inPhase(Phase.BEGINNING, Step.UNTAP)
            repeat(12) { builder = builder.withCardInLibrary(1, "Swamp") }
            repeat(12) { builder = builder.withCardInLibrary(2, "Swamp") }
            val game = builder.build()

            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

            val choice = game.resolveToModeChoice()
            withClue("All four modes are available the first time") {
                choice.options.size shouldBe 4
                choice.options shouldContain damageMode
                choice.options shouldContain loseMode
            }
            game.chooseMode(choice, damageMode)

            // The damage mode needs "any target" — point it at the opponent.
            game.selectTargets(listOf(game.player2Id)).error shouldBe null
            game.resolveStack()

            withClue("4 damage to the opponent") { game.getLifeTotal(2) shouldBe 16 }
            withClue("You gain 4 life") { game.getLifeTotal(1) shouldBe 24 }
        }

        test("a chosen mode is never offered again, and only the controller's upkeep triggers") {
            var builder = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Demonic Pact")
                .withLifeTotal(1, 20)
                .withLifeTotal(2, 20)
                .withActivePlayer(1)
                .inPhase(Phase.BEGINNING, Step.UNTAP)
            repeat(20) { builder = builder.withCardInLibrary(1, "Swamp") }
            repeat(20) { builder = builder.withCardInLibrary(2, "Swamp") }
            val game = builder.build()

            // Upkeep 1 — take "Draw two cards".
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            val first = game.resolveToModeChoice()
            first.options.size shouldBe 4
            val handBefore = game.handSize(1)
            game.chooseMode(first, drawMode)
            game.resolveStack()
            withClue("The draw mode drew two cards") {
                game.handSize(1) shouldBe handBefore + 2
            }

            // Through the opponent's turn: no trigger there ("your upkeep").
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.resolveStack()
            withClue("No modal choice pending on the opponent's upkeep") {
                (game.getPendingDecision() as? ChooseOptionDecision) shouldBe null
            }

            // Upkeep 2 (controller's) — the draw mode is gone.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            val second = game.resolveToModeChoice()
            withClue("The already-chosen draw mode is no longer offered") {
                second.options.size shouldBe 3
                second.options shouldNotContain drawMode
                second.options shouldContain discardMode
            }
        }

        test("once the three good modes are spent, the pact collects") {
            var builder = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Demonic Pact")
                .withCardsInHand(2, "Swamp", 4)
                .withLifeTotal(1, 20)
                .withLifeTotal(2, 20)
                .withActivePlayer(1)
                .inPhase(Phase.BEGINNING, Step.UNTAP)
            repeat(30) { builder = builder.withCardInLibrary(1, "Swamp") }
            repeat(30) { builder = builder.withCardInLibrary(2, "Swamp") }
            val game = builder.build()

            // Upkeep 1 — draw two.
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.chooseMode(game.resolveToModeChoice(), drawMode)
            game.resolveStack()

            // Upkeep 2 — target opponent discards two. With a single opponent the target is forced,
            // so resolution runs straight into the opponent's discard selection.
            game.toNextOwnUpkeep()
            val second = game.resolveToModeChoice()
            val opponentHandBefore = game.handSize(2)
            game.chooseMode(second, discardMode)
            game.resolveAnsweringCardSelections()
            withClue("The opponent discarded two cards") {
                game.handSize(2) shouldBe opponentHandBefore - 2
            }

            // Upkeep 3 — 4 damage / gain 4.
            game.toNextOwnUpkeep()
            val third = game.resolveToModeChoice()
            third.options.size shouldBe 2
            game.chooseMode(third, damageMode)
            game.selectTargets(listOf(game.player2Id)).error shouldBe null
            game.resolveAnsweringCardSelections()

            // Upkeep 4 — the contract's last clause is the only pick left.
            game.toNextOwnUpkeep()
            val fourth = game.resolveToModeChoice()
            withClue("Only 'You lose the game' remains") {
                fourth.options.size shouldBe 1
                fourth.options shouldContain loseMode
            }
            game.chooseMode(fourth, loseMode)
            game.resolveStack()

            withClue("Choosing the last clause loses the game — the opponent wins") {
                game.state.gameOver shouldBe true
                game.state.winnerId shouldBe game.player2Id
            }
        }
    }
}
