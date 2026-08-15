package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Vaultguard Trooper (EOE #166).
 *
 * "At the beginning of your end step, if you control two or more tapped creatures,
 *  you may discard your hand. If you do, draw two cards."
 *
 * The key case is the empty hand: discarding your hand is an action that always succeeds, even
 * when it discards zero cards, so answering "yes" with no cards in hand still draws two (same
 * templating as Narset, Jeskai Waymaster, whose official ruling reads "You may choose to discard
 * your hand even if your hand contains zero cards.").
 */
class VaultguardTrooperScenarioTest : ScenarioTestBase() {

    private fun baseScenario() = run {
        var builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Vaultguard Trooper", summoningSickness = false)
            .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
            .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        // Library fuel for the draws and step advances.
        repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
        repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
        builder
    }

    init {
        context("Vaultguard Trooper end-step trigger") {

            test("empty hand: choosing to discard still draws two cards") {
                val game = baseScenario().build()
                game.handSize(1) shouldBe 0

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("The may-discard trigger should prompt even with an empty hand") {
                    game.hasPendingDecision() shouldBe true
                }
                game.answerYesNo(true)
                game.resolveStack()

                withClue("Discarding an empty hand still counts as 'if you do' — draw two") {
                    game.handSize(1) shouldBe 2
                }
                game.graveyardSize(1) shouldBe 0
            }

            test("nonempty hand: discards everything, then draws two") {
                val game = baseScenario()
                    .withCardInHand(1, "Mountain")
                    .withCardInHand(1, "Mountain")
                    .withCardInHand(1, "Mountain")
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.hasPendingDecision() shouldBe true
                game.answerYesNo(true)
                game.resolveStack()

                game.handSize(1) shouldBe 2
                withClue("All three discarded cards should be in the graveyard") {
                    game.graveyardSize(1) shouldBe 3
                }
            }

            test("declining keeps the hand and draws nothing") {
                val game = baseScenario()
                    .withCardInHand(1, "Mountain")
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.hasPendingDecision() shouldBe true
                game.answerYesNo(false)
                game.resolveStack()

                game.handSize(1) shouldBe 1
                game.graveyardSize(1) shouldBe 0
            }

            test("fewer than two tapped creatures: the ability doesn't trigger") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vaultguard Trooper", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.hasPendingDecision() shouldBe false
                game.handSize(1) shouldBe 0
            }
        }
    }
}
