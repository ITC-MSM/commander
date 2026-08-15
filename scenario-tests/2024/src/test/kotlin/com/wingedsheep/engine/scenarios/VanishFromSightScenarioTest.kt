package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vanish from Sight (DSK #82) — "Target nonland permanent's owner puts it on their choice of the
 * top or bottom of their library. Surveil 1."
 *
 * Exercises `Effects.PutOnTopOrBottomOfLibrary` (the permanent's owner picks top/bottom) composed
 * with `Patterns.Library.surveil(1)` for the spell's controller.
 */
class VanishFromSightScenarioTest : ScenarioTestBase() {

    init {
        context("Vanish from Sight — bounce-to-library + surveil") {

            test("the targeted permanent's owner puts it into their library, then the caster surveils") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vanish from Sight")
                    .withLandsOnBattlefield(1, "Island", 4)
                    // The opponent controls a creature to be sent back to its owner's library.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    // Give the caster a library card so surveil has something to look at.
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Vanish from Sight"
                }
                val bears = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(game.player1Id, spellId, listOf(ChosenTarget.Permanent(bears)))
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The owner (Player2) chooses top or bottom of their library.
                withClue("Owner of the bounced permanent is prompted for top/bottom") {
                    (game.getPendingDecision() != null) shouldBe true
                    game.getPendingDecision()!!.playerId shouldBe game.player2Id
                }
                // Choose option 0 (top of library); the continuation moves the permanent.
                game.submitDecision(OptionChosenResponse(game.getPendingDecision()!!.id, 0))
                game.resolveStack()

                withClue("Grizzly Bears left the battlefield and is now in Player2's library") {
                    game.state.getBattlefield(game.player2Id).contains(bears) shouldBe false
                    game.state.getLibrary(game.player2Id).contains(bears) shouldBe true
                }

                // The spell then surveils 1 for the caster (a SelectCardsDecision with min 0).
                // Keep the top card (select none) and let the spell finish resolving.
                if (game.getPendingDecision() is SelectCardsDecision) {
                    game.skipSelection()
                    game.resolveStack()
                }
                withClue("the surveil decision is resolved (no decision left pending)") {
                    (game.getPendingDecision() is SelectCardsDecision) shouldBe false
                }
            }
        }
    }
}
