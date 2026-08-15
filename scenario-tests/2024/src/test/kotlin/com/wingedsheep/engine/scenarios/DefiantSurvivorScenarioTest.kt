package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Defiant Survivor (DSK #175) — {2}{G} 3/2 Creature — Human Survivor.
 *
 * "Survival — At the beginning of your second main phase, if this creature is tapped, manifest
 *  dread."
 *
 * Survival is an intervening-if postcombat-main trigger gated on the source being tapped; the
 * payoff is manifest dread (look at top two, manifest one as a face-down 2/2, the other to the
 * graveyard).
 */
class DefiantSurvivorScenarioTest : ScenarioTestBase() {

    init {
        context("Defiant Survivor — Survival manifest dread") {

            test("tapped at second main phase manifests dread: a face-down 2/2 enters, other card milled") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Defiant Survivor", tapped = true)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val battlefieldBefore = game.findPermanents("Grizzly Bears").size +
                    game.findPermanents("Hill Giant").size
                val graveBefore = game.graveyardSize(1)

                // Advance to the controller's second main phase; the Survival trigger goes on the
                // stack. Resolve until the manifest-dread card-selection decision surfaces.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                var guard = 0
                while (game.getPendingDecision() !is SelectCardsDecision && guard++ < 20) {
                    game.resolveStack()
                }

                val decision = game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected a SelectCardsDecision for manifest dread; got ${game.getPendingDecision()}")
                // Manifest the first option.
                val manifestPick = decision.options.first()
                game.submitDecision(CardsSelectedResponse(decisionId = decision.id, selectedCards = listOf(manifestPick)))
                game.resolveStack()

                withClue("A face-down 2/2 was manifested onto the battlefield") {
                    val faceDown = game.state.getEntity(manifestPick)?.get<FaceDownComponent>()
                    faceDown shouldBe FaceDownComponent
                    game.state.projectedState.getPower(manifestPick) shouldBe 2
                    game.state.projectedState.getToughness(manifestPick) shouldBe 2
                    game.state.projectedState.isCreature(manifestPick) shouldBe true
                }
                withClue("The other looked-at card was put into the graveyard") {
                    game.graveyardSize(1) shouldBe graveBefore + 1
                }
            }

            test("untapped at second main phase does not manifest dread") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Defiant Survivor", tapped = false)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val graveBefore = game.graveyardSize(1)

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Intervening-if (this creature is tapped) is false — no manifest dread") {
                    game.hasPendingDecision() shouldBe false
                    game.graveyardSize(1) shouldBe graveBefore
                }
            }
        }
    }
}
