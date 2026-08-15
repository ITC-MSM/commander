package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Scrabbling Skullcrab (DSK #71).
 *
 * Scrabbling Skullcrab — {U} Creature — Crab Skeleton, 0/3
 *   "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a
 *    Room, target player mills two cards."
 *
 * Verifies the enchantment-enters Eerie trigger mills the chosen target player two cards,
 * and that an opponent's enchantment entering does NOT trigger it ("an enchantment you control").
 */
class ScrabblingSkullcrabScenarioTest : ScenarioTestBase() {

    init {
        context("Scrabbling Skullcrab — Eerie (enchantment enters)") {

            test("an enchantment you control entering mills the chosen target player two cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scrabbling Skullcrab")
                    .withCardInHand(1, "Test Enchantment") // {1}{W}
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Hill Giant")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentLibraryBefore = game.librarySize(2)
                val opponentGraveBefore = game.graveyardSize(2)

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                // Resolving the enchantment surfaces the Eerie trigger's target choice.
                game.resolveStack()

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the Eerie mill trigger; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(game.player2Id))))
                game.resolveStack()

                withClue("The targeted opponent mills two cards") {
                    game.librarySize(2) shouldBe opponentLibraryBefore - 2
                    game.graveyardSize(2) shouldBe opponentGraveBefore + 2
                }
            }

            test("an opponent's enchantment entering does NOT trigger the Eerie mill") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scrabbling Skullcrab")
                    .withCardInHand(2, "Test Enchantment") // {1}{W}, controlled by the opponent
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(2, "Hill Giant")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val p1LibraryBefore = game.librarySize(1)
                val p2LibraryBefore = game.librarySize(2)

                val cast = game.castSpell(2, "Test Enchantment")
                withClue("Opponent casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("No Eerie mill trigger — the enchantment isn't the Skullcrab controller's") {
                    game.hasPendingDecision() shouldBe false
                    game.librarySize(1) shouldBe p1LibraryBefore
                    game.librarySize(2) shouldBe p2LibraryBefore
                }
            }
        }
    }
}
