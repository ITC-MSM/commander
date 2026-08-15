package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Glimmervoid (MRD #281):
 *
 *   Land
 *   "At the beginning of the end step, if you control no artifacts, sacrifice this land.
 *    {T}: Add one mana of any color."
 *
 * The interesting half is the intervening-if end-step trigger (CR 603.4): it must not even
 * trigger while its controller holds an artifact, and it must fire on *each* end step — including
 * an opponent's — when they don't.
 */
class GlimmervoidScenarioTest : ScenarioTestBase() {

    init {
        context("Glimmervoid") {

            test("is sacrificed at the end step when its controller controls no artifacts") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glimmervoid")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Glimmervoid starts on the battlefield") {
                    game.isOnBattlefield("Glimmervoid") shouldBe true
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("with no artifacts, the end-step trigger sacrifices it") {
                    game.isOnBattlefield("Glimmervoid") shouldBe false
                }
            }

            test("survives the end step while its controller controls an artifact") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Glimmervoid")
                    .withCardOnBattlefield(1, "Bonesplitter")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("the intervening-if clause is false, so the ability never triggers") {
                    game.isOnBattlefield("Glimmervoid") shouldBe true
                }
            }
        }
    }
}
