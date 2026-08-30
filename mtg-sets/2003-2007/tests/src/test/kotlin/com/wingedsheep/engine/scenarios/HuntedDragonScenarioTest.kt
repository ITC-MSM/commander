package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Hunted Dragon (RAV #131) — {3}{R}{R} Creature — Dragon 6/6.
 *
 *   Flying, haste
 *   When this creature enters, target opponent creates three 2/2 white Knight creature tokens with
 *   first strike.
 *
 * The Knights are the only member of the cycle whose tokens carry a keyword of their own, so this
 * test pins first strike on the created tokens as well as their controller — a token keyword that
 * silently dropped would make the drawback strictly weaker than printed.
 */
class HuntedDragonScenarioTest : ScenarioTestBase() {

    init {
        context("Hunted Dragon") {

            test("three 2/2 first-striking Knights enter under the targeted opponent's control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Dragon").isSuccess shouldBe true
                game.resolveStack()

                val dragon = game.findPermanent("Hunted Dragon")!!
                withClue("Hunted Dragon is a 6/6 with flying and haste") {
                    game.state.projectedState.getPower(dragon) shouldBe 6
                    game.state.projectedState.getToughness(dragon) shouldBe 6
                    game.state.projectedState.hasKeyword(dragon, Keyword.FLYING) shouldBe true
                    game.state.projectedState.hasKeyword(dragon, Keyword.HASTE) shouldBe true
                }

                val knights = game.findPermanents("Knight Token")
                withClue("All three Knights are created") { knights shouldHaveSize 3 }

                withClue("Every Knight is a 2/2 first striker under the opponent's control") {
                    knights.forEach { knight ->
                        game.state.getBattlefield(game.player2Id).contains(knight) shouldBe true
                        game.state.projectedState.getPower(knight) shouldBe 2
                        game.state.projectedState.getToughness(knight) shouldBe 2
                        game.state.projectedState
                            .hasKeyword(knight, Keyword.FIRST_STRIKE) shouldBe true
                    }
                }
            }
        }
    }
}
