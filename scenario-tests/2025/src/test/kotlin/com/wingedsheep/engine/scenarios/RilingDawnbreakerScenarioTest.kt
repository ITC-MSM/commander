package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Riling Dawnbreaker // Signaling Roar (TDM #21).
 *
 * Riling Dawnbreaker — {4}{W} Dragon, 3/4, Flying, vigilance.
 *   "At the beginning of combat on your turn, another target creature you control gets +1/+0 until end of turn."
 * Signaling Roar — {1}{W} Sorcery — Omen.
 *   "Create a 2/2 white Soldier creature token."
 */
class RilingDawnbreakerScenarioTest : ScenarioTestBase() {

    init {
        context("Riling Dawnbreaker creature face") {

            test("begin-combat trigger pumps another target creature you control +1/+0") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Riling Dawnbreaker")
                    .withCardOnBattlefield(1, "Centaur Courser") // 3/3 ally to pump
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ally = game.findPermanent("Centaur Courser")!!

                // Pass priority through the precombat main into the begin-combat step. The
                // turn-based trigger surfaces a target-selection decision; choose the ally
                // (the Dragon excludes itself via "another"), then let the trigger resolve.
                var guard = 0
                while (game.state.step != Step.BEGIN_COMBAT && guard++ < 30) {
                    if (game.hasPendingDecision()) {
                        game.selectTargets(listOf(ally))
                    } else {
                        game.passPriority()
                    }
                }
                // The trigger may still be on the stack awaiting its target / resolution.
                guard = 0
                while ((game.hasPendingDecision() || game.state.stack.isNotEmpty()) && guard++ < 30) {
                    if (game.hasPendingDecision()) {
                        game.selectTargets(listOf(ally))
                    } else {
                        game.passPriority()
                    }
                }

                val projected = StateProjector().project(game.state)
                withClue("Centaur Courser is pumped to 4/3 by the begin-combat trigger") {
                    projected.getPower(ally) shouldBe 4
                    projected.getToughness(ally) shouldBe 3
                }
            }
        }

        context("Signaling Roar Omen face") {

            test("creates a 2/2 white Soldier, then shuffles the Omen back into the library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Riling Dawnbreaker")
                    .withLandsOnBattlefield(1, "Plains", 2) // {1}{W}
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Riling Dawnbreaker"
                }
                val libraryBefore = game.librarySize(1) // 1

                val cast = game.execute(
                    CastSpell(playerId = game.player1Id, cardId = cardId, faceIndex = 0)
                )
                withClue("Casting Signaling Roar should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val token = game.findPermanent("Soldier Token") ?: game.findPermanent("Soldier")
                withClue("A 2/2 white Soldier token is created") {
                    val id = token!!
                    val projected = StateProjector().project(game.state)
                    projected.getPower(id) shouldBe 2
                    projected.getToughness(id) shouldBe 2
                }
                // Library: started at 1, +1 from the Omen shuffling back = 2.
                withClue("Signaling Roar shuffles back into the library") {
                    game.librarySize(1) shouldBe libraryBefore + 1
                    game.findCardsInLibrary(1, "Riling Dawnbreaker").size shouldBe 1
                    game.isOnBattlefield("Riling Dawnbreaker") shouldBe false
                }
            }
        }
    }
}
