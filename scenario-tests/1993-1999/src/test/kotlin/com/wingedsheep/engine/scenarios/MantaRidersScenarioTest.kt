package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Manta Riders (TMP).
 *
 * Oracle: "{U}: This creature gains flying until end of turn."
 *
 * Reuses [com.wingedsheep.sdk.dsl.Effects.GrantKeyword] targeting [EffectTarget.Self]; the
 * test confirms activating the ability grants flying via projected state.
 */
class MantaRidersScenarioTest : ScenarioTestBase() {

    init {
        context("Manta Riders — {U}: gains flying until end of turn") {
            test("does not have flying before activation") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Manta Riders", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val riders = game.findPermanent("Manta Riders")!!
                withClue("Manta Riders has no flying by default") {
                    game.state.projectedState.hasKeyword(riders, Keyword.FLYING) shouldBe false
                }
            }

            test("gains flying after activating the ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Manta Riders", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 1) // pays {U}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val riders = game.findPermanent("Manta Riders")!!
                val abilityId = cardRegistry.getCard("Manta Riders")!!.activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = riders, abilityId = abilityId)
                )
                withClue("Activating Manta Riders should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Manta Riders should have flying after activation") {
                    game.state.projectedState.hasKeyword(riders, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
