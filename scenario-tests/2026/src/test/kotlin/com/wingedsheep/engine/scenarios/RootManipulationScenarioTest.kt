package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Root Manipulation {3}{B}{G} Sorcery (Secrets of Strixhaven #222).
 *
 * "Until end of turn, creatures you control get +2/+2 and gain menace and
 *  'Whenever this creature attacks, you gain 1 life.'"
 *
 * Covers all three granted clauses applied to each creature the caster controls at resolution:
 * the +2/+2 buff, the menace keyword (via projected state), and the granted attack trigger
 * (gain 1 life when an affected creature attacks).
 */
class RootManipulationScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Root Manipulation — team-wide buff, menace, and attack lifegain") {

            test("creatures you control get +2/+2 and menace") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Root Manipulation")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Root Manipulation").error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears becomes 4/4 with menace") {
                    projector.getProjectedPower(game.state, bears) shouldBe 4
                    projector.getProjectedToughness(game.state, bears) shouldBe 4
                    projector.getProjectedKeywords(game.state, bears) shouldContainMenace true
                }
            }

            test("an affected creature attacking gains its controller 1 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Root Manipulation")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Root Manipulation").error shouldBe null
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.resolveStack()

                withClue("The granted attack trigger gains the controller 1 life (20 -> 21)") {
                    game.getLifeTotal(1) shouldBe 21
                }
            }
        }
    }

    private infix fun Set<Keyword>.shouldContainMenace(expected: Boolean) {
        this.contains(Keyword.MENACE) shouldBe expected
    }
}
