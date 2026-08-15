package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Unlucky Drop (LCI #82) — "Target artifact or creature's owner puts it on their choice of the
 * top or bottom of their library."
 *
 * Exercises `Effects.PutOnTopOrBottomOfLibrary` (the target's owner picks top/bottom) against both
 * a creature and a (non-creature) artifact target, and verifies the choosing player is the OWNER,
 * not the spell's caster.
 */
class UnluckyDropScenarioTest : ScenarioTestBase() {

    init {
        context("Unlucky Drop — artifact-or-creature bounce to owner's library") {

            test("targeting an opponent's creature: the OWNER chooses top of their library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Unlucky Drop")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Unlucky Drop"
                }
                val bears = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(game.player1Id, spellId, listOf(ChosenTarget.Permanent(bears)))
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The owner (Player2), not the caster, is prompted for top/bottom.
                withClue("Owner of the bounced creature is prompted for top/bottom") {
                    (game.getPendingDecision() != null) shouldBe true
                    game.getPendingDecision()!!.playerId shouldBe game.player2Id
                }
                // Option 0 = top of library.
                game.submitDecision(OptionChosenResponse(game.getPendingDecision()!!.id, 0))
                game.resolveStack()

                withClue("Grizzly Bears left the battlefield and is now on top of Player2's library") {
                    game.state.getBattlefield(game.player2Id).contains(bears) shouldBe false
                    game.state.getLibrary(game.player2Id).first() shouldBe bears
                }
            }

            test("targeting a non-creature artifact: the owner chooses bottom of their library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Unlucky Drop")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(2, "Bonesplitter")
                    // Give Player2 an existing library card so "bottom" is distinguishable from "top".
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Unlucky Drop"
                }
                val artifact = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Bonesplitter"
                }

                val cast = game.execute(
                    CastSpell(game.player1Id, spellId, listOf(ChosenTarget.Permanent(artifact)))
                )
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Owner of the bounced artifact is prompted for top/bottom") {
                    (game.getPendingDecision() != null) shouldBe true
                    game.getPendingDecision()!!.playerId shouldBe game.player2Id
                }
                // Option 1 = bottom of library.
                game.submitDecision(OptionChosenResponse(game.getPendingDecision()!!.id, 1))
                game.resolveStack()

                withClue("Bonesplitter left the battlefield and is on the bottom of Player2's library") {
                    game.state.getBattlefield(game.player2Id).contains(artifact) shouldBe false
                    game.state.getLibrary(game.player2Id).last() shouldBe artifact
                }
            }
        }
    }
}
