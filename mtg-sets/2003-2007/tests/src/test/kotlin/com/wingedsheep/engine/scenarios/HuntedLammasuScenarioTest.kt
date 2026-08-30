package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Hunted Lammasu (RAV #22) — {2}{W}{W} Creature — Lammasu 5/5.
 *
 *   Flying
 *   When this creature enters, target opponent creates a 4/4 black Horror creature token.
 *
 * The point of the cycle is *who* the token belongs to: the trigger's controller creates nothing,
 * and the 4/4 Horror enters under the targeted opponent's control. A token minted for the caster
 * would turn the drawback into an upside, so the test pins the controller, not just the count.
 */
class HuntedLammasuScenarioTest : ScenarioTestBase() {

    init {
        context("Hunted Lammasu") {

            test("the 4/4 Horror enters under the targeted opponent's control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Lammasu")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Lammasu").isSuccess shouldBe true
                game.resolveStack()

                val lammasu = game.findPermanent("Hunted Lammasu")!!
                withClue("Hunted Lammasu is a 5/5 flier") {
                    game.state.projectedState.getPower(lammasu) shouldBe 5
                    game.state.projectedState.getToughness(lammasu) shouldBe 5
                    game.state.projectedState.hasKeyword(lammasu, Keyword.FLYING) shouldBe true
                }

                val horrors = game.findPermanents("Horror Token")
                withClue("Exactly one Horror is created") { horrors shouldHaveSize 1 }

                val horror = horrors.single()
                withClue("The Horror belongs to the targeted opponent, not the caster") {
                    game.state.getBattlefield(game.player2Id).contains(horror) shouldBe true
                    game.state.getBattlefield(game.player1Id).contains(horror) shouldBe false
                }
                withClue("The Horror is a 4/4") {
                    game.state.projectedState.getPower(horror) shouldBe 4
                    game.state.projectedState.getToughness(horror) shouldBe 4
                }
            }
        }
    }
}
