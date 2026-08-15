package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Spiritcall Enthusiast // Scrollboost (Secrets of Strixhaven #33).
 *
 * Spiritcall Enthusiast ({2}{W}, 3/3, Cat Cleric):
 *   Whenever one or more tokens you control enter, this creature becomes prepared.
 *   //
 *   Scrollboost — {1}{W}, Sorcery: One or two target creatures each get +2/+2 until end of turn.
 *
 * Spiritcall Enthusiast does NOT enter prepared (no PREPARED keyword). It becomes prepared via its
 * token-enter trigger (`Effects.BecomePrepared`); casting the resulting Scrollboost copy unprepares
 * it and pumps one or two target creatures.
 */
class SpiritcallEnthusiastScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private fun TestGame.findExileCopy(playerNumber: Int, name: String): com.wingedsheep.sdk.model.EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    init {
        context("Spiritcall Enthusiast — becomes prepared when tokens you control enter") {

            test("does not enter prepared; a token entering prepares it, and Scrollboost pumps a creature") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spiritcall Enthusiast", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Dragon Fodder") // {1}{R} — makes two 1/1 Goblin tokens
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val spiritcall = game.findPermanent("Spiritcall Enthusiast")!!
                withClue("Spiritcall Enthusiast has no PREPARED keyword — must NOT enter prepared") {
                    game.state.getEntity(spiritcall)?.get<PreparedComponent>() shouldBe null
                }

                // Make tokens enter under your control -> Spiritcall becomes prepared.
                game.castSpell(1, "Dragon Fodder")
                game.resolveStack()

                withClue("a token entering prepares Spiritcall Enthusiast") {
                    game.state.getEntity(spiritcall)?.get<PreparedComponent>() shouldNotBe null
                }
                val copyId = game.findExileCopy(1, "Spiritcall Enthusiast")
                withClue("a Scrollboost prepare-spell copy should be in exile") {
                    copyId shouldNotBe null
                }

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Grizzly Bears starts as a 2/2") {
                    projector.getProjectedPower(game.state, bears) shouldBe 2
                }

                // Cast the Scrollboost copy from exile, targeting Grizzly Bears.
                game.execute(
                    CastSpell(
                        game.player1Id,
                        copyId!!,
                        listOf(ChosenTarget.Permanent(bears)),
                        faceIndex = 0,
                    )
                )
                game.resolveStack()

                withClue("Scrollboost gives the target +2/+2 until end of turn") {
                    projector.getProjectedPower(game.state, bears) shouldBe 4
                    projector.getProjectedToughness(game.state, bears) shouldBe 4
                }
                withClue("casting the Scrollboost copy unprepares Spiritcall Enthusiast") {
                    game.state.getEntity(spiritcall)?.get<PreparedComponent>() shouldBe null
                }
            }
        }
    }
}
