package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rise of the Dark Realms (M14 #111; reprinted in Foundations #183).
 *
 * Put all creature cards from all graveyards onto the battlefield under your control.
 *
 * Covers the sweep of every player's graveyard (Player.Each), the creature-only filter, and the
 * "under your control" placement. All primitives already exist (GatherCards from graveyards +
 * MoveCollection to the battlefield under the caster's control).
 */
class RiseOfTheDarkRealmsScenarioTest : ScenarioTestBase() {

    init {
        context("Rise of the Dark Realms") {

            test("reanimates every creature card from all graveyards under your control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Rise of the Dark Realms")
                    .withCardInGraveyard(1, "Grizzly Bears")   // your creature
                    .withCardInGraveyard(2, "Craw Wurm")       // opponent's creature
                    .withCardInGraveyard(1, "Lightning Bolt")  // non-creature: stays put
                    .withLandsOnBattlefield(1, "Swamp", 9)     // pays {7}{B}{B}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Rise of the Dark Realms").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val grizzly = game.findPermanent("Grizzly Bears")
                val crawWurm = game.findPermanent("Craw Wurm")

                withClue("Both creature cards are on the battlefield") {
                    (grizzly != null) shouldBe true
                    (crawWurm != null) shouldBe true
                }
                withClue("Both are under your (player 1's) control, even the opponent's creature") {
                    game.state.getEntity(grizzly!!)?.get<ControllerComponent>()?.playerId shouldBe game.player1Id
                    game.state.getEntity(crawWurm!!)?.get<ControllerComponent>()?.playerId shouldBe game.player1Id
                }
                withClue("The non-creature card stays in the graveyard") {
                    game.isInGraveyard(1, "Lightning Bolt") shouldBe true
                }
            }
        }
    }
}
