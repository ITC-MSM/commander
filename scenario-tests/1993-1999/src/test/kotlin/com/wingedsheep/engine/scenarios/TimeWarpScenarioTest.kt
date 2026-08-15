package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Time Warp (TMP).
 *
 * Oracle: "Target player takes an extra turn after this one."
 *
 * Reuses [com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect] with a player target.
 * In a 2-player game an extra turn is modeled by the other player skipping their next turn
 * ([SkipNextTurnComponent]). The test confirms the targeted player's opponent is set to skip.
 */
class TimeWarpScenarioTest : ScenarioTestBase() {

    init {
        context("Time Warp — target player takes an extra turn") {
            test("self-target: caster's opponent skips their next turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Time Warp")
                    .withLandsOnBattlefield(1, "Island", 5) // {3}{U}{U}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val result = game.castSpellTargetingPlayer(1, "Time Warp", targetPlayerNumber = 1)
                withClue("Casting Time Warp should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Player 1 takes the extra turn, so Player 2 (its opponent) skips a turn") {
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe true
                    game.state.getEntity(game.player1Id)?.has<SkipNextTurnComponent>() shouldBe false
                }
            }

            test("targeting the opponent: opponent takes the extra turn, caster skips") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Time Warp")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val result = game.castSpellTargetingPlayer(1, "Time Warp", targetPlayerNumber = 2)
                withClue("Casting Time Warp should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Player 2 takes the extra turn, so Player 1 (its opponent) skips a turn") {
                    game.state.getEntity(game.player1Id)?.has<SkipNextTurnComponent>() shouldBe true
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe false
                }
            }
        }
    }
}
