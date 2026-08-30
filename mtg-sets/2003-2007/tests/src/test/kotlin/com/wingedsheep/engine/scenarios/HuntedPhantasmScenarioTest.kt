package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Hunted Phantasm (RAV #55) — {1}{U}{U} Creature — Spirit 4/6.
 *
 *   This creature can't be blocked.
 *   When this creature enters, target opponent creates five 1/1 red Goblin creature tokens.
 *
 * Five is the largest token count in the cycle, so it is worth proving the count survives the
 * opponent-controller route rather than collapsing to one. The evasion half is the card-level
 * [AbilityFlag.CANT_BE_BLOCKED] and is checked on the projected state.
 */
class HuntedPhantasmScenarioTest : ScenarioTestBase() {

    init {
        context("Hunted Phantasm") {

            test("five 1/1 Goblins enter under the targeted opponent's control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Phantasm")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Phantasm").isSuccess shouldBe true
                game.resolveStack()

                val phantasm = game.findPermanent("Hunted Phantasm")!!
                withClue("Hunted Phantasm is an unblockable 4/6") {
                    game.state.projectedState.getPower(phantasm) shouldBe 4
                    game.state.projectedState.getToughness(phantasm) shouldBe 6
                    game.state.projectedState
                        .hasKeyword(phantasm, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }

                val goblins = game.findPermanents("Goblin Token")
                withClue("All five Goblins are created") { goblins shouldHaveSize 5 }

                withClue("Every Goblin belongs to the targeted opponent") {
                    goblins.forEach { goblin ->
                        game.state.getBattlefield(game.player2Id).contains(goblin) shouldBe true
                        game.state.projectedState.getPower(goblin) shouldBe 1
                        game.state.projectedState.getToughness(goblin) shouldBe 1
                    }
                }
            }
        }
    }
}
