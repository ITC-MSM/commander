package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Rocket Launcher (ATQ #63).
 *
 * {4} Artifact
 * "{2}: This artifact deals 1 damage to any target. Destroy this artifact at the beginning of the
 *  next end step. Activate only if you've controlled this artifact continuously since the beginning
 *  of your most recent turn."
 *
 * Exercises the ControlledSinceYourMostRecentTurn activation restriction (artifact summoning sickness):
 * unavailable the turn it enters / control changes, available once controlled since your turn began.
 */
class RocketLauncherScenarioTest : ScenarioTestBase() {

    init {
        fun rocketAbilityId() =
            cardRegistry.getCard("Rocket Launcher")!!.script.activatedAbilities[0].id

        context("Rocket Launcher") {

            test("cannot be activated the turn it enters (summoning sickness)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rocket Launcher", summoningSickness = true)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rocket = game.findPermanent("Rocket Launcher")!!

                withClue("The {2} ability is not a legal action while summoning-sick") {
                    val activations = game.getLegalActions(1).map { it.action }
                        .filterIsInstance<ActivateAbility>()
                        .filter { it.sourceId == rocket }
                    activations shouldBe emptyList()
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rocket,
                        abilityId = rocketAbilityId(),
                        targets = listOf(entityIdToChosenTarget(game.state, game.player2Id))
                    )
                )
                withClue("Direct activation is rejected by the control-since-your-turn restriction") {
                    result.error shouldNotBe null
                }
                withClue("No damage was dealt") {
                    game.getLifeTotal(2) shouldBe 20
                }
            }

            test("can be activated once controlled since your most recent turn began") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rocket Launcher", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rocket = game.findPermanent("Rocket Launcher")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rocket,
                        abilityId = rocketAbilityId(),
                        targets = listOf(entityIdToChosenTarget(game.state, game.player2Id))
                    )
                )
                withClue("Activation succeeds: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Deals 1 damage to the chosen target (opponent)") {
                    game.getLifeTotal(2) shouldBe 19
                }
            }
        }
    }
}
