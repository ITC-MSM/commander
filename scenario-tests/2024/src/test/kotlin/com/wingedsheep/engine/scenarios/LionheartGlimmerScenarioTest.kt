package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Lionheart Glimmer (DSK #19).
 *
 * Lionheart Glimmer — {3}{W}{W} Enchantment Creature — Cat Glimmer, 2/5
 *   "Ward {2}
 *    Whenever you attack, creatures you control get +1/+1 until end of turn."
 *
 * Verifies the attack trigger buffs every creature the attacking player controls (not just
 * attackers) until end of turn, and that the Ward keyword is granted.
 */
class LionheartGlimmerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Lionheart Glimmer — Whenever you attack") {

            test("buffs all creatures you control +1/+1 until end of turn, including a non-attacker") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lionheart Glimmer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val glimmer = game.findPermanent("Lionheart Glimmer").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                // Attack with only the Glimmer; the Bears stays back as a non-attacker.
                val attack = game.declareAttackers(mapOf("Lionheart Glimmer" to 2))
                withClue("Declaring attackers should succeed: ${attack.error}") { attack.error shouldBe null }
                game.resolveStack()

                val projected = projector.project(game.state)
                withClue("Lionheart Glimmer should be 3/6 (2/5 +1/+1)") {
                    projected.getPower(glimmer) shouldBe 3
                    projected.getToughness(glimmer) shouldBe 6
                }
                withClue("Non-attacking Grizzly Bears should also be buffed to 3/3 (2/2 +1/+1)") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 3
                }
            }

            test("the buff wears off at end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lionheart Glimmer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val glimmer = game.findPermanent("Lionheart Glimmer").shouldNotBeNull()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Lionheart Glimmer" to 2))
                game.resolveStack()

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                val projected = projector.project(game.state)
                withClue("After the turn ends, Lionheart Glimmer is back to its printed 2/5") {
                    projected.getPower(glimmer) shouldBe 2
                    projected.getToughness(glimmer) shouldBe 5
                }
            }

            test("has Ward as a granted keyword") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lionheart Glimmer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val glimmer = game.findPermanent("Lionheart Glimmer").shouldNotBeNull()
                val projected = projector.project(game.state)
                withClue("Lionheart Glimmer has Ward") {
                    projected.hasKeyword(glimmer, Keyword.WARD).shouldBeTrue()
                }
            }
        }
    }
}
