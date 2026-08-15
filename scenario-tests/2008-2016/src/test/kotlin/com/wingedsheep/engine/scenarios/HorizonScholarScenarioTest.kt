package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Horizon Scholar (THS #51).
 *
 * Horizon Scholar — {5}{U} Creature — Sphinx, 4/4, Flying.
 *   "When this creature enters, scry 2."
 *
 * This is the first card to exercise the mtgish-tooling `_Action = Scry` mapping
 * (bridge `composed("Scry", …)` + emitter `on("Scry")` → `Patterns.Library.scry(N)`),
 * here driven from a triggered ability rather than a spell. The scry pipeline asks the
 * controller which of the looked-at cards to put on the bottom (a [SelectCardsDecision])
 * and then to order the rest on top (a [ReorderLibraryDecision]).
 */
class HorizonScholarScenarioTest : ScenarioTestBase() {

    init {
        context("Horizon Scholar ETB scry 2") {

            // Library top→bottom: Mountain, Forest, then three Islands.
            fun buildGame() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Horizon Scholar")
                .withLandsOnBattlefield(1, "Island", 6)
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("puts both looked-at cards on the bottom") {
                val game = buildGame()

                game.castSpell(1, "Horizon Scholar").error shouldBe null
                game.resolveStack() // creature enters → ETB scry 2 → SelectCardsDecision

                val decision = game.getPendingDecision()
                withClue("Scry 2 presents a card-selection over the top two cards") {
                    (decision is SelectCardsDecision) shouldBe true
                    (decision as SelectCardsDecision).options.size shouldBe 2
                }
                // Put every looked-at card on the bottom.
                game.selectCards((decision as SelectCardsDecision).options).error shouldBe null
                game.resolveStack()

                withClue("Library is undisturbed in size") {
                    game.librarySize(1) shouldBe 5
                }
                withClue("Bottomed cards are no longer on top — an Island surfaces") {
                    game.cardNameAtTop(1) shouldBe "Island"
                }
                withClue("Mountain and Forest were moved to the bottom") {
                    game.cardNameAtBottom(1) shouldBe "Forest"
                }
                withClue("Horizon Scholar resolved onto the battlefield") {
                    game.isOnBattlefield("Horizon Scholar") shouldBe true
                }
            }

            test("can keep the looked-at cards on top in chosen order") {
                val game = buildGame()

                game.castSpell(1, "Horizon Scholar").error shouldBe null
                game.resolveStack()

                // Put none on the bottom…
                val select = game.getPendingDecision()
                (select is SelectCardsDecision) shouldBe true
                game.skipSelection().error shouldBe null

                // …then order the two kept cards back on top, Mountain first.
                val reorder = game.getPendingDecision()
                withClue("Keeping cards on top asks for their order") {
                    (reorder is ReorderLibraryDecision) shouldBe true
                }
                game.submitDecision(
                    OrderedResponse((reorder as ReorderLibraryDecision).id, reorder.cards)
                ).error shouldBe null
                game.resolveStack()

                withClue("The top card stays the one ordered first") {
                    game.cardNameAtTop(1) shouldBe "Mountain"
                }
                withClue("Nothing left the library") {
                    game.librarySize(1) shouldBe 5
                }
            }
        }
    }

    private fun TestGame.libraryIds(playerNumber: Int): List<EntityId> =
        state.getLibrary(if (playerNumber == 1) player1Id else player2Id)

    private fun TestGame.cardNameAtTop(playerNumber: Int): String? =
        state.getEntity(libraryIds(playerNumber).first())?.get<CardComponent>()?.name

    private fun TestGame.cardNameAtBottom(playerNumber: Int): String? =
        state.getEntity(libraryIds(playerNumber).last())?.get<CardComponent>()?.name
}
