package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dandân — covers the new state-triggered ability primitive (CR 603.8).
 *
 * Oracle:
 *  - This creature can't attack unless defending player controls an Island.
 *  - When you control no Islands, sacrifice this creature.
 *
 * The second clause is a state-triggered ability: it fires whenever the controller transitions
 * from "controls ≥1 Island" to "controls 0 Islands". The engine's [StateTriggerPoller] polls
 * battlefield permanents at each priority pass and emits a [PendingTrigger] for the
 * false→true transition; the per-entity [StateTriggerLatchesComponent] prevents the trigger
 * from re-firing every priority pass while the condition stays true.
 */
class DandanScenarioTest : ScenarioTestBase() {

    init {
        context("Dandân — state-triggered \"sacrifice if you control no Islands\"") {

            test("does not fire when controller still controls at least one Island") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dandân", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.resolveStack()

                withClue("Dandân should still be on the battlefield while controller has Islands") {
                    game.isOnBattlefield("Dandân") shouldBe true
                }
            }

            test("fires when controller has no Islands; Dandân is sacrificed at next priority pass") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dandân", summoningSickness = false)
                    // No Islands controlled — the state trigger should fire immediately.
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Advance through priority passes. The first priority cycle invokes the
                // state-trigger poller, the trigger goes on the stack, and Dandân is sacrificed
                // when it resolves.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Dandân's state-triggered ability should sacrifice it") {
                    game.isOnBattlefield("Dandân") shouldBe false
                }
            }
        }
    }
}
