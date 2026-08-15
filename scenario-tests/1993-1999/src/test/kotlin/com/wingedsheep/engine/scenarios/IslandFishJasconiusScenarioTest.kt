package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Island Fish Jasconius's "When you control no Islands, sacrifice ~"
 * state-triggered ability. The doesn't-untap + upkeep-pay-to-untap clauses follow the
 * same shape as Brass Man (covered elsewhere); this test focuses on the new primitive.
 */
class IslandFishJasconiusScenarioTest : ScenarioTestBase() {

    init {
        context("Island Fish Jasconius — \"sacrifice when you control no Islands\"") {

            test("is sacrificed at next priority pass when controller has no Islands") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Island Fish Jasconius", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Island Fish Jasconius should be sacrificed (no Islands controlled)") {
                    game.isOnBattlefield("Island Fish Jasconius") shouldBe false
                }
            }

            test("stays in play while controller still controls an Island") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Island Fish Jasconius", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("Island Fish Jasconius should still be on the battlefield") {
                    game.isOnBattlefield("Island Fish Jasconius") shouldBe true
                }
            }
        }
    }
}
