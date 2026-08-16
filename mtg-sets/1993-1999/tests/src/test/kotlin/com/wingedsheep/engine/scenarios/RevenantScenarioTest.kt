package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Revenant (STH #68).
 *
 * "Revenant's power and toughness are each equal to the number of creature cards in your graveyard."
 *
 * Regression: the generated draft counted creatures *on the battlefield* instead, which is a
 * different card. Argentum Assay's differential reported it the day the grammar could read the
 * line, and these tests pin the zone, the filter and whose graveyard is counted.
 */
class RevenantScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Revenant") {

            test("P/T counts creature cards in your own graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Revenant", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Hill Giant")
                    .withCardInGraveyard(1, "Ancestral Recall") // noncreature — must not count
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val revenantId = game.findPermanent("Revenant")!!
                val projected = stateProjector.project(game.state)

                withClue("two creature cards in your graveyard = 2 power") {
                    projected.getPower(revenantId) shouldBe 2
                }
                withClue("two creature cards in your graveyard = 2 toughness") {
                    projected.getToughness(revenantId) shouldBe 2
                }
            }

            test("creatures on the battlefield and an opponent's graveyard do not count") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Revenant", summoningSickness = false)
                    // The bug this pins: three creatures on your own battlefield…
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardOnBattlefield(1, "Scathe Zombies")
                    // …and creature cards in the *opponent's* graveyard.
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val revenantId = game.findPermanent("Revenant")!!
                val projected = stateProjector.project(game.state)

                withClue("nothing in your own graveyard = 0 power") {
                    projected.getPower(revenantId) shouldBe 0
                }
                withClue("nothing in your own graveyard = 0 toughness") {
                    projected.getToughness(revenantId) shouldBe 0
                }
            }
        }
    }
}
