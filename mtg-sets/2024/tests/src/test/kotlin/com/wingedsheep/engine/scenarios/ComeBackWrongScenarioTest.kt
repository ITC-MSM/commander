package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Come Back Wrong (DSK #86) — {2}{B} Sorcery.
 *
 * "Destroy target creature. If a creature card is put into a graveyard this way, return it to the
 *  battlefield under your control. Sacrifice it at the beginning of your next end step."
 *
 * Substituted into this batch in place of Valgavoth's Onslaught (which needs an as-yet-unbuilt
 * dynamic-X repeat-N-times loop with cross-iteration entity accumulation — `add-feature` territory).
 *
 * Pure composition (Zero Point Ballad / Kheru Lich Lord shape): destroy the target via a
 * MoveType.Destroy pipeline capturing what actually hit the graveyard, reanimate the nontoken
 * creature card under your control, and arm a delayed end-step sacrifice on the reanimated permanent.
 */
class ComeBackWrongScenarioTest : ScenarioTestBase() {

    init {
        context("Come Back Wrong — destroy, reanimate under your control, delayed sacrifice") {

            test("reanimates the destroyed creature under your control, then sacrifices it at the next end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Come Back Wrong")
                    .withCardOnBattlefield(2, "Grizzly Bears") // opponent's 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Bears start under player 2's control") {
                    game.state.getEntity(bears)?.get<com.wingedsheep.engine.state.components.identity.ControllerComponent>()
                        ?.playerId shouldBe game.player2Id
                }

                game.castSpell(1, "Come Back Wrong", bears).error shouldBe null
                game.resolveStack()

                withClue("It's now on the battlefield under player 1's control (the same card entity)") {
                    game.state.getEntity(bears)
                        ?.get<com.wingedsheep.engine.state.components.identity.ControllerComponent>()
                        ?.playerId shouldBe game.player1Id
                }

                // Advance to this turn's end step — the delayed sacrifice fires.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Sacrificed at the beginning of the controller's next end step") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true // owner's graveyard
                }
            }

            test("a destroyed token is not reanimated (it ceases to exist, not a creature card in a graveyard)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Come Back Wrong")
                    // A creature token: when destroyed it goes to the graveyard then ceases to exist,
                    // so there is no creature *card* to return.
                    .withCardOnBattlefield(2, "Grizzly Bears", isToken = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val token = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Come Back Wrong", token).error shouldBe null
                game.resolveStack()

                withClue("The token is destroyed and not returned to anyone's battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }
        }
    }
}
