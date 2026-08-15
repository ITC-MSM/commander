package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Dictate of Kruphix (JOU #37, reprinted FDN #587).
 *
 * {1}{U}{U} Enchantment — Flash
 * "At the beginning of each player's draw step, that player draws an additional card."
 *
 * Verifies the "each player" scope: both the controller and the opponent draw an extra
 * card on their own draw step (the additional draw goes to [Player.TriggeringPlayer], the
 * player whose draw step it is).
 */
class DictateOfKruphixScenarioTest : ScenarioTestBase() {

    init {
        context("Dictate of Kruphix") {

            test("each player draws an extra card on their own draw step") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dictate of Kruphix")
                    // Start on the opponent's turn so the first draw step we reach is a
                    // regular (non-first-turn) draw step for the controller.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(20) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(20) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                // --- Controller's draw step (player 1) ---
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                val p1BeforeDraw = game.handSize(1)

                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()
                withClue("Controller draws the turn-based card + Dictate's additional card (+2)") {
                    game.handSize(1) shouldBe p1BeforeDraw + 2
                }

                // --- Opponent's draw step (player 2) ---
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                val p2BeforeDraw = game.handSize(2)

                game.passUntilPhase(Phase.BEGINNING, Step.DRAW)
                game.resolveStack()
                withClue("Opponent also draws the turn-based card + Dictate's additional card (+2)") {
                    game.handSize(2) shouldBe p2BeforeDraw + 2
                }
            }
        }
    }
}
