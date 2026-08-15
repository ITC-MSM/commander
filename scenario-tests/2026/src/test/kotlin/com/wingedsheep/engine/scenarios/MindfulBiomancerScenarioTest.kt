package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Mindful Biomancer (SOS).
 *
 * {1}{G} Creature — Dryad Druid, 2/2
 * "When this creature enters, you gain 1 life.
 *  {2}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn."
 */
class MindfulBiomancerScenarioTest : ScenarioTestBase() {

    init {
        context("Mindful Biomancer") {

            test("ETB: entering the battlefield gains its controller 1 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Mindful Biomancer")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Mindful Biomancer").error shouldBe null
                game.resolveStack()

                withClue("the biomancer should be on the battlefield") {
                    game.isOnBattlefield("Mindful Biomancer") shouldBe true
                }
                withClue("the ETB trigger should gain 1 life") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 1
                }
            }

            test("activating {2}{G} gives it +2/+2 until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mindful Biomancer", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val biomancer = game.findPermanent("Mindful Biomancer")!!
                val abilityId = cardRegistry.getCard("Mindful Biomancer")!!
                    .script.activatedAbilities[0].id

                // Baseline 2/2.
                game.state.projectedState.getPower(biomancer) shouldBe 2
                game.state.projectedState.getToughness(biomancer) shouldBe 2

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = biomancer,
                        abilityId = abilityId
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                withClue("Mindful Biomancer should be 4/4 after +2/+2") {
                    game.state.projectedState.getPower(biomancer) shouldBe 4
                    game.state.projectedState.getToughness(biomancer) shouldBe 4
                }
            }

            test("the pump ability can only be activated once each turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mindful Biomancer", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val biomancer = game.findPermanent("Mindful Biomancer")!!
                val abilityId = cardRegistry.getCard("Mindful Biomancer")!!
                    .script.activatedAbilities[0].id

                // Before activating, the pump ability is a legal action.
                withClue("the pump ability should be available before any activation") {
                    game.getLegalActions(1).find {
                        it.actionType == "ActivateAbility" &&
                            (it.action as? ActivateAbility)?.sourceId == biomancer
                    } shouldNotBe null
                }

                // First activation succeeds; resolve it.
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = biomancer,
                        abilityId = abilityId
                    )
                ).error shouldBe null
                game.resolveStack()

                // With OncePerTurn, the ability must no longer be offered this turn even though
                // we have plenty of untapped mana left.
                withClue("the pump ability should NOT be available a second time this turn") {
                    game.getLegalActions(1).find {
                        it.actionType == "ActivateAbility" &&
                            (it.action as? ActivateAbility)?.sourceId == biomancer
                    } shouldBe null
                }
            }
        }
    }
}
