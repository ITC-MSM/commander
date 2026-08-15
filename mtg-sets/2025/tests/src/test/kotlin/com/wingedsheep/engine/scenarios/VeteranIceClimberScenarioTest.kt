package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Veteran Ice Climber (TDM #64).
 *
 * "{1}{U} Creature — Human Scout 1/3.
 *  Vigilance. This creature can't be blocked.
 *  Whenever this creature attacks, up to one target player mills cards equal to this
 *  creature's power."
 *
 * Verifies the attack-trigger mill scales with the creature's power: a base 1/3 mills one
 * card from the chosen player, and a pumped (+1/+1 counter) 2/3 mills two. The mill amount
 * is read at resolution via DynamicAmounts.sourcePower().
 */
class VeteranIceClimberScenarioTest : ScenarioTestBase() {

    init {
        context("Veteran Ice Climber attack trigger") {

            test("mills the targeted player a number of cards equal to its power (1)") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Veteran Ice Climber", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.declareAttackers(mapOf("Veteran Ice Climber" to 2))
                game.resolveStack()

                // "up to one target player" — choose the opponent if prompted.
                if (game.hasPendingDecision() && game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(game.player2Id))
                }
                game.resolveStack()

                withClue("A 1-power Veteran Ice Climber mills exactly one card") {
                    game.findCardsInGraveyard(2, "Forest").size shouldBe 1
                }
                withClue("Library should have shrunk by one (5 -> 4)") {
                    game.findCardsInLibrary(2, "Forest").size shouldBe 4
                }
            }

            test("mill amount scales with power (a +1/+1 counter makes it mill two)") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Veteran Ice Climber", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                // Pump to 2/4 with a +1/+1 counter so the mill should be 2.
                val climber = game.findPermanent("Veteran Ice Climber")!!
                game.state = game.state.updateEntity(climber) { c ->
                    c.with(
                        com.wingedsheep.engine.state.components.battlefield.CountersComponent()
                            .withAdded(com.wingedsheep.sdk.core.CounterType.PLUS_ONE_PLUS_ONE, 1)
                    )
                }

                game.declareAttackers(mapOf("Veteran Ice Climber" to 2))
                game.resolveStack()

                if (game.hasPendingDecision() && game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(game.player2Id))
                }
                game.resolveStack()

                withClue("A 2-power Veteran Ice Climber mills two cards") {
                    game.findCardsInGraveyard(2, "Forest").size shouldBe 2
                }
                withClue("Library should have shrunk by two (5 -> 3)") {
                    game.findCardsInLibrary(2, "Forest").size shouldBe 3
                }
            }
        }
    }
}
