package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Extravagant Replication (NCC #25; reprinted in Foundations #154).
 *
 * At the beginning of your upkeep, create a token that's a copy of another target nonland
 * permanent you control.
 *
 * Covers the your-upkeep trigger and the token copy of a targeted nonland permanent. All
 * primitives already exist (YourUpkeep trigger, CreateTokenCopyOfTarget, the OtherNonlandPermanent
 * you-control target filter).
 */
class ExtravagantReplicationScenarioTest : ScenarioTestBase() {

    init {
        context("Extravagant Replication") {

            test("your upkeep creates a token copy of another nonland permanent you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Extravagant Replication")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    // Start on the opponent's turn so we can pass into our own next upkeep.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Only the original Grizzly Bears exists before the upkeep trigger") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 1
                }

                val original = game.findPermanent("Grizzly Bears")!!

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                // The upkeep trigger targets "another target nonland permanent you control".
                var guard = 0
                while (guard++ < 12) {
                    when (game.getPendingDecision()) {
                        is ChooseTargetsDecision -> game.selectTargets(listOf(original))
                        null -> if (game.state.stack.isNotEmpty()) game.resolveStack() else break
                        else -> break
                    }
                }

                withClue("A token copy of Grizzly Bears now sits beside the original") {
                    game.findAllPermanents("Grizzly Bears").size shouldBe 2
                }
            }
        }
    }
}
