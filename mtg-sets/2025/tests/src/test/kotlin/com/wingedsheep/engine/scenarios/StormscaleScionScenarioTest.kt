package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Stormscale Scion (TDM #123).
 *
 * "Flying
 *  Other Dragons you control get +1/+1.
 *  Storm (...)"
 *
 * Verifies the flying keyword and the you-control Dragon lord buff (other Dragons only,
 * not the Scion itself, and not opponents' Dragons). Storm is exercised by the shared
 * keyword machinery and is not re-verified here.
 */
class StormscaleScionScenarioTest : ScenarioTestBase() {

    init {
        context("Stormscale Scion") {

            test("has flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stormscale Scion")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scion = game.findPermanent("Stormscale Scion")!!
                game.state.projectedState.hasKeyword(scion, Keyword.FLYING) shouldBe true
            }

            test("buffs other Dragons you control but not itself or non-Dragons") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stormscale Scion")
                    .withCardOnBattlefield(1, "Grizzly Bears")    // non-Dragon you control
                    .withCardOnBattlefield(1, "Volcanic Dragon")  // another Dragon you control
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scion = game.findPermanent("Stormscale Scion")!!
                val dragon = game.findPermanent("Volcanic Dragon")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = game.state.projectedState

                withClue("Volcanic Dragon (4/4) should be buffed to 5/5 by the Scion") {
                    projected.getPower(dragon) shouldBe 5
                    projected.getToughness(dragon) shouldBe 5
                }
                withClue("Stormscale Scion should not buff itself (stays 4/4)") {
                    projected.getPower(scion) shouldBe 4
                    projected.getToughness(scion) shouldBe 4
                }
                withClue("Non-Dragon Grizzly Bears should stay 2/2") {
                    projected.getPower(bears) shouldBe 2
                    projected.getToughness(bears) shouldBe 2
                }
            }

            test("does not buff opponent's Dragons") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stormscale Scion")
                    .withCardOnBattlefield(2, "Volcanic Dragon")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Volcanic Dragon")!!
                withClue("Opponent's Volcanic Dragon should stay 4/4") {
                    game.state.projectedState.getPower(dragon) shouldBe 4
                    game.state.projectedState.getToughness(dragon) shouldBe 4
                }
            }
        }
    }
}
