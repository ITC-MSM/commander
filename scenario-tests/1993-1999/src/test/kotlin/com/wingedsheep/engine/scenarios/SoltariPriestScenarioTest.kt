package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Soltari Priest (TMP).
 *
 * Oracle: "Protection from red" + "Shadow".
 *
 * Reuses [com.wingedsheep.sdk.scripting.KeywordAbility.Protection] and the [Keyword.SHADOW]
 * keyword (combat handled by the engine's ShadowRule). The test confirms both keywords are
 * present in projected state.
 */
class SoltariPriestScenarioTest : ScenarioTestBase() {

    init {
        context("Soltari Priest — Protection from red + Shadow") {
            test("has shadow and protection from red, but not other protections") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Soltari Priest", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val priest = game.findPermanent("Soltari Priest")!!
                val projected = game.state.projectedState

                withClue("Soltari Priest should have shadow") {
                    projected.hasKeyword(priest, Keyword.SHADOW) shouldBe true
                }
                withClue("Soltari Priest should have protection from red") {
                    projected.hasKeyword(priest, "PROTECTION_FROM_RED") shouldBe true
                }
                withClue("Soltari Priest should not have protection from other colors") {
                    projected.hasKeyword(priest, "PROTECTION_FROM_WHITE") shouldBe false
                    projected.hasKeyword(priest, "PROTECTION_FROM_BLUE") shouldBe false
                }
            }
        }
    }
}
