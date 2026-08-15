package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Skulking Killer (VOW #130).
 *
 * "{3}{B} Creature — Vampire Assassin  (4/2)
 *  When this creature enters, target creature an opponent controls gets -2/-2 until end of
 *  turn if that opponent controls no other creatures."
 *
 * The intervening "if that opponent controls no other creatures" is a **resolution-time**
 * check, modeled as a `ConditionalEffect` gating the -2/-2 on
 * `AggregateBattlefield(Player.ControllerOf("target creature"), Creature) == 1` — i.e. the
 * target's controller controls exactly one creature (the target itself). These tests pin
 * both branches, and in doing so exercise the `DynamicAmountEvaluator` `ControllerOf`
 * resolution that counts permanents controlled by the chosen target's controller.
 */
class SkulkingKillerScenarioTest : ScenarioTestBase() {

    init {
        context("Skulking Killer ETB -2/-2") {

            test("applies -2/-2 when the opponent controls only the targeted creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Skulking Killer")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardOnBattlefield(2, "Centaur Courser") // 3/3, the opponent's only creature
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!

                game.castSpell(1, "Skulking Killer").error shouldBe null
                game.resolveStack() // Skulking Killer enters → ETB trigger asks for a target

                val result = game.selectTargets(listOf(courser))
                withClue("targeting the opponent's creature is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("opponent controls no other creature, so -2/-2 applies: 3/3 -> 1/1") {
                    game.state.projectedState.getPower(courser) shouldBe 1
                    game.state.projectedState.getToughness(courser) shouldBe 1
                }
            }

            test("does NOT apply -2/-2 when the opponent controls another creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Skulking Killer")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardOnBattlefield(2, "Centaur Courser") // 3/3, the target
                    .withCardOnBattlefield(2, "Savannah Lions")  // a second creature the opponent controls
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!

                game.castSpell(1, "Skulking Killer").error shouldBe null
                game.resolveStack() // ETB trigger asks for a target

                val result = game.selectTargets(listOf(courser))
                withClue("targeting the opponent's creature is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("opponent controls another creature, so -2/-2 does NOT apply: stays 3/3") {
                    game.state.projectedState.getPower(courser) shouldBe 3
                    game.state.projectedState.getToughness(courser) shouldBe 3
                }
            }
        }
    }
}
