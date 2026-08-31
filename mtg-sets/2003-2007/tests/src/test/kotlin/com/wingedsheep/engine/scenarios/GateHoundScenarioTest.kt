package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Gate Hound (Ravnica: City of Guilds).
 *
 * Oracle: "Creatures you control have vigilance as long as this creature is enchanted."
 *
 * The card is a lord behind a gate, and the gate is the only thing worth proving: a
 * `ConditionalStaticAbility` whose condition is `SourceMatches(Any.enchanted())` has to be
 * re-evaluated by the projector, not read once. So: no Aura → no vigilance anywhere; an Aura on the
 * Hound → the whole team including the Hound; an Aura on a *different* creature → still nothing.
 */
class GateHoundScenarioTest : ScenarioTestBase() {

    init {
        context("Gate Hound — vigilance while enchanted") {

            test("grants nothing while the Hound is unenchanted") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gate Hound")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hound = game.findPermanent("Gate Hound")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("no Aura attached → the gate is shut") {
                    game.state.projectedState.hasKeyword(hound, Keyword.VIGILANCE) shouldBe false
                    game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe false
                }
            }

            test("an Aura on the Hound gives the whole team vigilance, the Hound included") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gate Hound")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Gate Hound")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hound = game.findPermanent("Gate Hound")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("the grant is 'creatures you control', not 'other creatures'") {
                    game.state.projectedState.hasKeyword(hound, Keyword.VIGILANCE) shouldBe true
                }
                game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe true
            }

            test("an Aura on some other creature does not open the gate") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gate Hound")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hound = game.findPermanent("Gate Hound")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("the condition reads the Hound, not any enchanted creature") {
                    game.state.projectedState.hasKeyword(hound, Keyword.VIGILANCE) shouldBe false
                    game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe false
                }
            }
        }
    }
}
