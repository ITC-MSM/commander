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
 * Scenario tests for Scavenger Regent // Exude Toxin (TDM #90).
 *
 * Scavenger Regent — {3}{B} Dragon, 4/4, Flying, Ward—Discard a card.
 * Exude Toxin — {X}{B}{B} Sorcery — Omen.
 *   "Each non-Dragon creature gets -X/-X until end of turn.
 *    (Then shuffle this card into its owner's library.)"
 *
 * Confirms the Omen face wipes non-Dragon creatures with -X/-X (Dragons untouched), then shuffles
 * itself into the library rather than resolving to the battlefield.
 */
class ScavengerRegentScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Exude Toxin Omen face") {

            test("each non-Dragon creature gets -X/-X; Dragons are unaffected; the Omen shuffles back") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Scavenger Regent")
                    .withLandsOnBattlefield(1, "Swamp", 4) // {X=2}{B}{B}
                    // A non-Dragon: Hill Giant 3/3 — survives -2/-2 at 1/1.
                    .withCardOnBattlefield(1, "Hill Giant")
                    // A non-Dragon controlled by the opponent — "each" hits all creatures.
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2 → dies to -2/-2
                    // A Dragon: a second Scavenger Regent creature face — unaffected.
                    .withCardOnBattlefield(2, "Scavenger Regent") // 4/4 Dragon
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Scavenger Regent"
                }
                val giant = game.findPermanent("Hill Giant")!!
                val dragon = game.findPermanent("Scavenger Regent")!!
                val libraryBefore = game.librarySize(1)

                // Cast the Omen face (faceIndex = 0) with X = 2.
                val cast = game.execute(
                    CastSpell(playerId = game.player1Id, cardId = cardId, xValue = 2, faceIndex = 0)
                )
                withClue("Casting Exude Toxin should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The non-Dragon Grizzly Bears (2/2) is destroyed by -2/-2") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("The non-Dragon Hill Giant survives at 1/1 after -2/-2") {
                    projector.getProjectedPower(game.state, giant) shouldBe 1
                    projector.getProjectedToughness(game.state, giant) shouldBe 1
                }
                withClue("The Dragon is unaffected (stays 4/4)") {
                    projector.getProjectedPower(game.state, dragon) shouldBe 4
                    projector.getProjectedToughness(game.state, dragon) shouldBe 4
                }
                // Library: the Omen card shuffles back in (+1).
                withClue("Exude Toxin shuffles itself into the library, not the battlefield") {
                    game.librarySize(1) shouldBe libraryBefore + 1
                    game.findCardsInLibrary(1, "Scavenger Regent").size shouldBe 1
                }
            }
        }
    }
}
