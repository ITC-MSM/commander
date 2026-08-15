package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.EnteredThisTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Erg Raiders' end-step self-damage trigger.
 *
 * Oracle: "At the beginning of your end step, if this creature didn't attack this turn,
 * it deals 2 damage to you unless it came under your control this turn."
 *
 * Three cases:
 *  1. Settled (no attack, didn't enter this turn) → triggers, 2 damage to controller.
 *  2. Attacked this turn → doesn't trigger.
 *  3. Just entered (summoning-sick) → doesn't trigger.
 */
class ErgRaidersScenarioTest : ScenarioTestBase() {

    init {
        context("Erg Raiders end-step self-damage") {

            test("deals 2 damage to controller when it didn't attack and wasn't new") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Erg Raiders")
                    .withActivePlayer(1)
                    .build()

                val startLife = game.getLifeTotal(1)
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Erg Raiders should deal 2 to its controller") {
                    game.getLifeTotal(1) shouldBe startLife - 2
                }
            }

            test("does not trigger when Erg Raiders attacked this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Erg Raiders")
                    .withActivePlayer(1)
                    .build()

                val startLife = game.getLifeTotal(1)
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Erg Raiders" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Erg Raiders attacked → no self-damage trigger; controller's life unchanged") {
                    game.getLifeTotal(1) shouldBe startLife
                }
            }

            test("does not trigger when Erg Raiders came under control this turn (summoning sickness proxy)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Erg Raiders", summoningSickness = true)
                    .withActivePlayer(1)
                    .build()

                // Mark Erg Raiders as having entered this turn (the proxy for the
                // "came under your control this turn" oracle clause).
                val raidersId = game.findPermanent("Erg Raiders")!!
                game.state = game.state.updateEntity(raidersId) { it.with(EnteredThisTurnComponent) }

                val startLife = game.getLifeTotal(1)
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Erg Raiders is new this turn → no self-damage; controller's life unchanged") {
                    game.getLifeTotal(1) shouldBe startLife
                }
            }
        }
    }
}
