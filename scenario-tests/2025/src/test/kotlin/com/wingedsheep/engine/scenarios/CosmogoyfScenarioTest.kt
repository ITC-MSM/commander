package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Cosmogoyf (EOE) — {B}{G} Creature — Elemental Lhurgoyf.
 *
 * "Cosmogoyf's power is equal to the number of cards you own in exile and its toughness is
 *  equal to that number plus 1."
 *
 * Tarmogoyf-style CDA composed from existing primitives: dynamic power/toughness via
 * `DynamicAmounts.zone(Player.You, Zone.EXILE).count()`, with a +1 toughness offset.
 */
class CosmogoyfScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Cosmogoyf's */*+1 stats track cards in its controller's exile") {

            test("with no cards in exile, Cosmogoyf is 0/1") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cosmogoyf")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Cosmogoyf")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(goyf) shouldBe 0
                projected.getToughness(goyf) shouldBe 1
            }

            test("with three cards in controller's exile, Cosmogoyf is 3/4") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cosmogoyf")
                    .withCardInExile(1, "Grizzly Bears")
                    .withCardInExile(1, "Hill Giant")
                    .withCardInExile(1, "Shock")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Cosmogoyf")!!
                val projected = stateProjector.project(game.state)
                withClue("Power = cards in your exile = 3") {
                    projected.getPower(goyf) shouldBe 3
                }
                withClue("Toughness = that number + 1 = 4") {
                    projected.getToughness(goyf) shouldBe 4
                }
            }

            test("cards in the OPPONENT's exile do not count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cosmogoyf")
                    .withCardInExile(2, "Grizzly Bears")
                    .withCardInExile(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Cosmogoyf")!!
                val projected = stateProjector.project(game.state)
                projected.getPower(goyf) shouldBe 0
                projected.getToughness(goyf) shouldBe 1
            }
        }
    }
}
