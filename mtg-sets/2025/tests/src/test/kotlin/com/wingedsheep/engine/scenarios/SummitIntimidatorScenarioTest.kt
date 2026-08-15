package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Summit Intimidator (TDM #125).
 *
 * "Reach
 *  When this creature enters, target creature can't block this turn."
 */
class SummitIntimidatorScenarioTest : ScenarioTestBase() {

    init {
        context("Summit Intimidator") {

            test("has reach") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Summit Intimidator")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val intimidator = game.findPermanent("Summit Intimidator")!!
                game.state.projectedState.hasKeyword(intimidator, Keyword.REACH) shouldBe true
            }

            test("ETB makes target creature unable to block this turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Summit Intimidator")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpell(1, "Summit Intimidator")
                withClue("Casting Summit Intimidator should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB targets the opponent's creature.
                if (game.hasPendingDecision()) {
                    game.selectTargets(listOf(bears))
                    game.resolveStack()
                }

                withClue("Targeted Grizzly Bears should not be able to block this turn") {
                    game.state.projectedState.cantBlock(bears) shouldBe true
                }
            }
        }
    }
}
