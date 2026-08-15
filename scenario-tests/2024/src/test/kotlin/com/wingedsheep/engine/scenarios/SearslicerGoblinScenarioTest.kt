package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Searslicer Goblin ({1}{R}, 2/1 Goblin Warrior) — Raid: at the beginning of your end step,
 * if you attacked this turn, create a 1/1 red Goblin creature token.
 *
 * Verifies the intervening-if on the end-step trigger: a token is made only when the player
 * attacked this turn.
 */
class SearslicerGoblinScenarioTest : ScenarioTestBase() {

    init {
        context("Searslicer Goblin — Raid") {

            test("creates a Goblin token at end step when you attacked this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Searslicer Goblin")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Searslicer Goblin" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("attacked this turn → Raid makes one 1/1 Goblin token") {
                    game.findPermanents("Goblin Token").size shouldBe 1
                }
            }

            test("creates no token when you did not attack") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Searslicer Goblin")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("did not attack → no token") {
                    game.findPermanents("Goblin Token").size shouldBe 0
                }
            }
        }
    }
}
