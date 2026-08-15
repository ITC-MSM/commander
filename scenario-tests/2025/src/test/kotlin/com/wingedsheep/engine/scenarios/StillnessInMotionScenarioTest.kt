package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stillness in Motion (TDM #59).
 *
 * "{1}{U} Enchantment.
 *  At the beginning of your upkeep, mill three cards. Then if your library has no cards in it,
 *  exile this enchantment and put five cards from your graveyard on top of your library in any order."
 */
class StillnessInMotionScenarioTest : ScenarioTestBase() {

    init {
        context("Stillness in Motion upkeep") {

            test("mills three, and when the library empties exiles itself and refills the library from the graveyard") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stillness in Motion")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Exactly two cards in the controller's library so milling 3 empties it.
                repeat(2) { builder = builder.withCardInLibrary(1, "Island") }
                // Graveyard fuel: more than five cards so we put exactly five back.
                repeat(7) { builder = builder.withCardInGraveyard(1, "Plains") }
                // Opponent needs library so they don't deck during step advances.
                repeat(10) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                // Choose five graveyard cards to put back, then order them on top.
                (game.state.pendingDecision as? SelectCardsDecision)?.let { sel ->
                    game.selectCards(sel.options.take(5))
                    game.resolveStack()
                }
                (game.state.pendingDecision as? ReorderLibraryDecision)?.let { reorder ->
                    game.submitDecision(OrderedResponse(reorder.id, reorder.cards))
                    game.resolveStack()
                }

                val libSize = game.state.getLibrary(game.player1Id).size
                withClue("Library was empty after milling 3 from a 2-card library, then refilled with five graveyard cards") {
                    libSize shouldBe 5
                }
                withClue("Stillness in Motion exiled itself") {
                    game.findPermanent("Stillness in Motion") shouldBe null
                    game.state.getZone(game.player1Id, Zone.EXILE).any {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Stillness in Motion"
                    } shouldBe true
                }
            }

            test("when the library does not empty, only the mill happens") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stillness in Motion")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Plenty of library so mill 3 leaves cards behind.
                repeat(10) { builder = builder.withCardInLibrary(1, "Island") }
                repeat(5) { builder = builder.withCardInGraveyard(1, "Plains") }
                repeat(10) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val startLib = game.state.getLibrary(game.player1Id).size

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                if (game.hasPendingDecision()) game.resolveStack()

                withClue("Library shrank by exactly the three milled cards (upkeep precedes the draw step)") {
                    game.state.getLibrary(game.player1Id).size shouldBe startLib - 3
                }
                withClue("Enchantment stayed on the battlefield — library was not empty") {
                    (game.findPermanent("Stillness in Motion") != null) shouldBe true
                }
            }
        }
    }
}
