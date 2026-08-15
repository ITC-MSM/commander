package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Neriv, Heart of the Storm (TDM #210).
 *
 * "Flying
 *  If a creature you control that entered this turn would deal damage, it deals twice
 *  that much damage instead."
 *
 * Verifies the damage-doubling replacement effect:
 *  - a creature you control that entered this turn (Raging Goblin, cast this turn with
 *    haste) deals double combat damage,
 *  - a creature you control that did NOT enter this turn deals normal damage,
 *  - an opponent's creature is unaffected.
 */
class NerivHeartOfTheStormScenarioTest : ScenarioTestBase() {

    init {
        context("Neriv, Heart of the Storm") {

            test("has flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Neriv, Heart of the Storm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val neriv = game.findPermanent("Neriv, Heart of the Storm")!!
                game.state.projectedState.hasKeyword(neriv, Keyword.FLYING) shouldBe true
            }

            test("a creature that entered this turn deals double combat damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Neriv, Heart of the Storm")
                    .withCardInHand(1, "Raging Goblin")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startLife = game.getLifeTotal(2)

                // Cast Raging Goblin (1/1 haste) — it has entered this turn.
                game.castSpell(1, "Raging Goblin").error shouldBe null
                game.resolveStack()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Raging Goblin" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Raging Goblin (1 power) entered this turn → 2 combat damage doubled") {
                    game.getLifeTotal(2) shouldBe startLife - 2
                }
            }

            test("a creature that did not enter this turn deals normal damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Neriv, Heart of the Storm")
                    .withCardOnBattlefield(1, "Grizzly Bears") // placed pre-game, did not enter this turn
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startLife = game.getLifeTotal(2)

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Grizzly Bears did not enter this turn → normal 2 combat damage") {
                    game.getLifeTotal(2) shouldBe startLife - 2
                }
            }

            test("an opponent's creature that entered this turn is unaffected") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Neriv, Heart of the Storm")
                    .withCardInHand(2, "Raging Goblin")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val startLife = game.getLifeTotal(1)

                // Opponent (player 2) casts Raging Goblin this turn and attacks player 1.
                game.castSpell(2, "Raging Goblin").error shouldBe null
                game.resolveStack()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Raging Goblin" to 1)).error shouldBe null
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Opponent's Goblin is not 'a creature you control' for Neriv → normal 1 damage") {
                    game.getLifeTotal(1) shouldBe startLife - 1
                }
            }
        }
    }
}
