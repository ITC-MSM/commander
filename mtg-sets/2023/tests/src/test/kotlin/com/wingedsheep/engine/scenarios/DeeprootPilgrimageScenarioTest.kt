package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Deeproot Pilgrimage (LCI #52) — {1}{U} Enchantment, Rare.
 *
 * "Whenever one or more nontoken Merfolk you control become tapped, create a 1/1 blue Merfolk
 *  creature token with hexproof."
 *
 * The headline is the **batch** semantics (CR 603.2c) via the new `TapEvent(batch = true)` trigger:
 * tapping several Merfolk simultaneously (attacking) makes a *single* token, not one per Merfolk —
 * the over-count a per-tap trigger would produce. Also pins that the filter discriminates (a
 * non-Merfolk tapping makes none).
 */
class DeeprootPilgrimageScenarioTest : ScenarioTestBase() {

    init {
        context("Deeproot Pilgrimage") {

            test("two Merfolk tapping simultaneously make exactly one token (batch)") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Deeproot Pilgrimage")
                    .withCardOnBattlefield(1, "Coral Merfolk")
                    .withCardOnBattlefield(1, "Sandbar Merfolk")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                // Both attackers tap as one simultaneous batch when attackers are declared.
                game.declareAttackers(mapOf("Coral Merfolk" to 2, "Sandbar Merfolk" to 2))
                    .error shouldBe null
                game.resolveStack()

                withClue("batch trigger fires once for the simultaneous tap of two Merfolk") {
                    game.findPermanents("Merfolk Token").size shouldBe 1
                }
            }

            test("a single Merfolk tapping makes one token") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Deeproot Pilgrimage")
                    .withCardOnBattlefield(1, "Coral Merfolk")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Coral Merfolk" to 2)).error shouldBe null
                game.resolveStack()

                withClue("one Merfolk tapping still makes a token") {
                    game.findPermanents("Merfolk Token").size shouldBe 1
                }
            }

            test("a non-Merfolk tapping makes no token") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Deeproot Pilgrimage")
                    .withCardOnBattlefield(1, "Grizzly Bears")   // a Bear, not a Merfolk
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("the trigger's filter excludes non-Merfolk taps") {
                    game.findPermanents("Merfolk Token").size shouldBe 0
                }
            }
        }
    }
}
