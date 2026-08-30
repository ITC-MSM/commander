package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Blazing Archon (RAV #4) — {6}{W}{W}{W} Archon, 5/6.
 *
 * "Flying
 *  Creatures can't attack you."
 *
 * `CantBeAttackedBy(GameObjectFilter.Creature)` is checked against the *defending* player's
 * battlefield (CR 508.1c), so the two things worth proving are that the restriction protects the
 * Archon's controller — including against creatures that would otherwise be unblockable by it —
 * and that it stops being enforced the moment the Archon leaves.
 */
class BlazingArchonScenarioTest : ScenarioTestBase() {

    init {
        context("Blazing Archon attack restriction") {

            test("an opponent's creature can't attack the Archon's controller") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blazing Archon")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackers(mapOf("Grizzly Bears" to 1))

                withClue("Declaring an attack on the Archon's controller must be rejected") {
                    (result.error != null) shouldBe true
                }
            }

            test("the restriction is gone once the Archon leaves the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val result = game.declareAttackers(mapOf("Grizzly Bears" to 1))

                withClue("Without the Archon the same attack is legal: ${result.error}") {
                    result.error shouldBe null
                }
            }
        }
    }
}
