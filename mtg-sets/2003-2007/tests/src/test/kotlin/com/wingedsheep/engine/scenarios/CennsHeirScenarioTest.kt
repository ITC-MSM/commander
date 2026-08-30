package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cenn's Heir (LRW #8, {1}{W}, Creature — Kithkin Soldier 1/1).
 *
 *   Whenever this creature attacks, it gets +1/+1 until end of turn for each other attacking Kithkin.
 *
 * The count is an `excludeSelf` aggregate, so the two things worth proving are that the Heir does not
 * count itself (a lone attack leaves it a 1/1, not a 2/2) and that a *non*-attacking Kithkin sitting
 * back does not feed it either.
 */
class CennsHeirScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Cenn's Heir") {

            test("attacking alone leaves it a 1/1 — it never counts itself") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Cenn's Heir", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val heir = game.findPermanent("Cenn's Heir")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Cenn's Heir" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Zero other attacking Kithkin is +0/+0") {
                    val after = stateProjector.project(game.state)
                    after.getPower(heir) shouldBe 1
                    after.getToughness(heir) shouldBe 1
                }
            }

            test("two other attacking Kithkin make it a 3/3") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Cenn's Heir", summoningSickness = false)
                    .withCardOnBattlefield(1, "Goldmeadow Harrier", summoningSickness = false)
                    .withCardOnBattlefield(1, "Kinsbaile Skirmisher", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val heir = game.findPermanent("Cenn's Heir")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf(
                        "Cenn's Heir" to 2,
                        "Goldmeadow Harrier" to 2,
                        "Kinsbaile Skirmisher" to 2
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("Two other attacking Kithkin is +2/+2 on a 1/1") {
                    val after = stateProjector.project(game.state)
                    after.getPower(heir) shouldBe 3
                    after.getToughness(heir) shouldBe 3
                }
            }

            test("a Kithkin left at home is not counted, and a non-Kithkin attacker is not either") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Cenn's Heir", summoningSickness = false)
                    .withCardOnBattlefield(1, "Goldmeadow Harrier", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val heir = game.findPermanent("Cenn's Heir")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                // The Harrier stays home; the Bears attack but are no Kithkin.
                game.declareAttackers(mapOf("Cenn's Heir" to 2, "Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Neither an idle Kithkin nor an attacking non-Kithkin counts") {
                    val after = stateProjector.project(game.state)
                    after.getPower(heir) shouldBe 1
                    after.getToughness(heir) shouldBe 1
                }
            }
        }
    }
}
