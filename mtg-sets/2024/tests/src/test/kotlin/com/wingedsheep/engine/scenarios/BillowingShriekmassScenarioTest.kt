package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Billowing Shriekmass (FDN) — {3}{B} 2/3 Spirit.
 * Flying; ETB mill three; Threshold — +2/+1 while seven or more cards are in your graveyard.
 *
 * Covers the enters-the-battlefield mill (composed [com.wingedsheep.sdk.dsl.Patterns.Library.mill])
 * and the graveyard-count conditional static buff.
 */
class BillowingShriekmassScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private fun power(game: TestGame, id: EntityId) = projector.getProjectedPower(game.state, id)
    private fun toughness(game: TestGame, id: EntityId) = projector.getProjectedToughness(game.state, id)

    init {
        context("Billowing Shriekmass") {

            test("enters and mills three cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Billowing Shriekmass")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.graveyardSize(1) shouldBe 0

                game.castSpell(1, "Billowing Shriekmass").error shouldBe null
                game.resolveStack()

                withClue("ETB should have milled exactly three cards into the graveyard") {
                    game.graveyardSize(1) shouldBe 3
                }
                game.isOnBattlefield("Billowing Shriekmass") shouldBe true
            }

            test("threshold buffs it to 4/4 with seven cards in the graveyard") {
                val builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Billowing Shriekmass")
                repeat(7) { builder.withCardInGraveyard(1, "Swamp") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shriekmass = game.findPermanent("Billowing Shriekmass")!!
                withClue("2/3 base + threshold +2/+1 = 4/4") {
                    power(game, shriekmass) shouldBe 4
                    toughness(game, shriekmass) shouldBe 4
                }
            }

            test("stays 2/3 with only six cards in the graveyard") {
                val builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Billowing Shriekmass")
                repeat(6) { builder.withCardInGraveyard(1, "Swamp") }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shriekmass = game.findPermanent("Billowing Shriekmass")!!
                withClue("threshold not met below seven cards") {
                    power(game, shriekmass) shouldBe 2
                    toughness(game, shriekmass) shouldBe 3
                }
            }
        }
    }
}
