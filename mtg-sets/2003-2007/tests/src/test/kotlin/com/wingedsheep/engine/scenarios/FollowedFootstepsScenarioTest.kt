package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Followed Footsteps (RAV #51) — {3}{U}{U} Enchantment — Aura.
 *
 * "Enchant creature. At the beginning of your upkeep, create a token that's a copy of enchanted
 * creature."
 *
 * The copy is taken from `EffectTarget.EnchantedCreature` at resolution, so the token is a copy
 * of whatever the Aura is on *then* — and it is created under the Aura's controller, which is the
 * case worth pinning: enchanting an opponent's creature hands *you* the copy, not them.
 */
class FollowedFootstepsScenarioTest : ScenarioTestBase() {

    init {
        context("Followed Footsteps") {

            test("copies the enchanted creature on its controller's upkeep") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Followed Footsteps", "Grizzly Bears")
                    // Start on Bob's turn so the next upkeep reached is Alice's.
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.findPermanents("Grizzly Bears").size shouldBe 1

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                val bears = game.findPermanents("Grizzly Bears")
                withClue("a token copy joins the original") {
                    bears.size shouldBe 2
                    bears.forEach {
                        game.state.projectedState.getPower(it) shouldBe 2
                        game.state.projectedState.getToughness(it) shouldBe 2
                    }
                }
            }

            test("the token is created under the Aura's controller, even on a stolen host") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    // Bob's creature, Alice's Aura on it.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(1, "Followed Footsteps", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                val bears = game.findPermanents("Grizzly Bears")
                bears.size shouldBe 2
                withClue("exactly one of the two is Alice's — the token she created") {
                    bears.count { game.state.projectedState.getController(it) == game.player1Id } shouldBe 1
                }
            }

            test("it does not fire on the opponent's upkeep") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Followed Footsteps", "Grizzly Bears")
                    // Alice is active, so the next upkeep reached is Bob's.
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("\"your upkeep\" is Alice's, and this one is Bob's") {
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                }
            }
        }
    }
}
