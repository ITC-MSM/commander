package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dreadfeast Demon (VOW #108).
 *
 * "{5}{B}{B} Creature — Demon  (6/6)
 *  Flying
 *  At the beginning of your end step, sacrifice a non-Demon creature. If you do, create a token
 *  that's a copy of this creature."
 *
 * Exercises the gated end-step trigger: the copy is created only when a non-Demon creature is
 * actually sacrificed (CR 608.2 "if you do"). With no non-Demon fodder — only Demons on the
 * battlefield — the sacrifice does nothing and no copy is made. Confirms the Demon itself is never
 * eligible fodder (the `notSubtype(Demon)` filter).
 */
class DreadfeastDemonScenarioTest : ScenarioTestBase() {

    init {
        context("Dreadfeast Demon end-step sacrifice") {

            test("sacrifices a non-Demon creature and creates a copy of itself") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dreadfeast Demon", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("start: one Demon, one Grizzly Bears") {
                    game.findPermanents("Dreadfeast Demon").size shouldBe 1
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                }

                // Advance to the end step; the trigger goes on the stack and resolves.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the only non-Demon creature (Grizzly Bears) is sacrificed") {
                    game.findPermanents("Grizzly Bears").size shouldBe 0
                }
                withClue("a token copy of Dreadfeast Demon is created — now two Demons") {
                    game.findPermanents("Dreadfeast Demon").size shouldBe 2
                }
            }

            test("does NOT create a copy when there is no non-Demon creature to sacrifice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dreadfeast Demon", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("start: only the Demon, no non-Demon fodder") {
                    game.findPermanents("Dreadfeast Demon").size shouldBe 1
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("no non-Demon creature to sacrifice, so no copy is created — still one Demon") {
                    game.findPermanents("Dreadfeast Demon").size shouldBe 1
                }
            }

            test("lets the controller choose which non-Demon creature to sacrifice, then copies") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dreadfeast Demon", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Centaur Courser", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                // The mandatory sacrifice has two eligible non-Demon creatures, so the controller
                // must pick one; choose the Grizzly Bears.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("with two non-Demon creatures, the controller is prompted to choose one") {
                    game.getPendingDecision().shouldNotBeNull()
                }
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("the chosen Grizzly Bears is sacrificed; the Centaur Courser survives") {
                    game.findPermanents("Grizzly Bears").size shouldBe 0
                    game.findPermanents("Centaur Courser").size shouldBe 1
                }
                withClue("a token copy of Dreadfeast Demon is created — now two Demons") {
                    game.findPermanents("Dreadfeast Demon").size shouldBe 2
                }
            }
        }
    }
}
