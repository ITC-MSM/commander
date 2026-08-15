package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Overlord of the Mistmoors (DSK #23) — {5}{W}{W} Enchantment Creature — Avatar Horror 6/6,
 * Impending 4—{2}{W}{W}.
 *
 * "Whenever this permanent enters or attacks, create two 2/1 white Insect creature tokens with
 * flying."
 *
 * Impending itself is exercised by ImpendingMechanicTest; these tests cover the body ability via
 * both the enters trigger and the attacks trigger (cast for the normal mana cost so it enters as a
 * creature immediately). Tokens are matched by their Insect subtype (token name is engine-derived),
 * mirroring TheSwarmweaverScenarioTest.
 */
class OverlordOfTheMistmoorsScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Overlord of the Mistmoors — enters/attacks: create two 2/1 white flying Insects") {
            test("the enters trigger creates two 2/1 white flying Insect tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Overlord of the Mistmoors")
                    .withLandsOnBattlefield(1, "Plains", 7)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Overlord of the Mistmoors").error shouldBe null
                game.resolveStack()

                val projected = projector.project(game.state)
                val insects = game.state.getBattlefield()
                    .filter { projected.getSubtypes(it).contains("Insect") }

                withClue("Exactly two Insect tokens are created") {
                    insects.size shouldBe 2
                }
                insects.forEach { id ->
                    withClue("Each Insect token is 2/1 white with flying") {
                        projector.getProjectedPower(game.state, id) shouldBe 2
                        projector.getProjectedToughness(game.state, id) shouldBe 1
                        projected.getColors(id) shouldBe setOf("WHITE")
                        projected.hasKeyword(id, Keyword.FLYING) shouldBe true
                    }
                }
            }

            test("the attacks trigger creates two more 2/1 white flying Insect tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Overlord of the Mistmoors")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // It's already on the battlefield (no enters trigger fired here), so the
                // battlefield starts with no Insect tokens.
                val before = projector.project(game.state).let { p ->
                    game.state.getBattlefield().count { p.getSubtypes(it).contains("Insect") }
                }
                withClue("No Insect tokens before combat") { before shouldBe 0 }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Overlord of the Mistmoors" to 2))
                game.resolveStack()

                val projected = projector.project(game.state)
                val insects = game.state.getBattlefield()
                    .filter { projected.getSubtypes(it).contains("Insect") }

                withClue("The attacks trigger creates two Insect tokens") {
                    insects.size shouldBe 2
                }
                insects.forEach { id ->
                    withClue("Each Insect token is 2/1 white with flying") {
                        projector.getProjectedPower(game.state, id) shouldBe 2
                        projector.getProjectedToughness(game.state, id) shouldBe 1
                        projected.getColors(id) shouldBe setOf("WHITE")
                        projected.hasKeyword(id, Keyword.FLYING) shouldBe true
                    }
                }
            }
        }
    }
}
