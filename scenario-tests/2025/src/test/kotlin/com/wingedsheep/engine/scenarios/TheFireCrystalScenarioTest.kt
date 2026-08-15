package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Fire Crystal (FIN #135).
 *
 * {2}{R}{R} Legendary Artifact
 *  - Red spells you cast cost {1} less to cast.
 *  - Creatures you control have haste.
 *  - {4}{R}{R}, {T}: Create a token that's a copy of target creature you control. Sacrifice it at
 *    the beginning of the next end step.
 *
 * These tests cover the haste-granting static and the token-copy activated ability with its
 * delayed end-step sacrifice. The {1}-less cost reduction reuses the existing ModifySpellCost
 * primitive (covered by the Invasion leech cards) so it isn't re-exercised here.
 */
class TheFireCrystalScenarioTest : ScenarioTestBase() {

    init {
        context("The Fire Crystal") {

            test("grants haste to a creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Fire Crystal")
                    // Summoning-sick on purpose: only the crystal's haste static should matter.
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Grizzly Bears gains haste from The Fire Crystal") {
                    game.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true
                }
            }

            test("{4}{R}{R}, {T}: makes a token copy that is sacrificed at the next end step") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "The Fire Crystal", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 6) // pays {4}{R}{R}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crystal = game.findPermanent("The Fire Crystal")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val abilityId = cardRegistry.getCard("The Fire Crystal")!!
                    .script.activatedAbilities[0].id

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = crystal,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, bears)),
                    )
                )
                withClue("Activating The Fire Crystal should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("A token copy of Grizzly Bears is created (2 Grizzly Bears now)") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 2
                }

                // The delayed trigger sacrifices the token at the beginning of the next end step.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("The token copy is sacrificed at the next end step; only the original remains") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 1
                }
            }
        }
    }
}
