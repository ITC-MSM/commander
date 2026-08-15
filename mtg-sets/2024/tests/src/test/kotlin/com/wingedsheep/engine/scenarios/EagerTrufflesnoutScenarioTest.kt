package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Eager Trufflesnout ({2}{G}, 4/2 Boar) — Trample; whenever it deals combat damage to a
 * player, create a Food token.
 *
 * Verifies the combat-damage trigger produces exactly one Food when it connects with the
 * defending player, and produces none when it never deals combat damage to a player.
 */
class EagerTrufflesnoutScenarioTest : ScenarioTestBase() {

    init {
        context("Eager Trufflesnout") {

            test("creates a Food token when it deals combat damage to a player") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Eager Trufflesnout")
                    .withActivePlayer(1)
                    .build()

                val opponentLife = game.getLifeTotal(2)

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Eager Trufflesnout" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("4 combat damage to the unblocked defender") {
                    game.getLifeTotal(2) shouldBe opponentLife - 4
                }
                withClue("dealing combat damage to a player makes one Food") {
                    game.findPermanents("Food").size shouldBe 1
                }
            }

            test("makes no Food when it never attacks") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Eager Trufflesnout")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("no combat damage dealt → no Food") {
                    game.findPermanents("Food").size shouldBe 0
                }
            }
        }
    }
}
