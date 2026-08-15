package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Bride's Gown (VOW #4) — {1}{W} Artifact — Equipment
 *
 *   Equipped creature gets +2/+0. It gets an additional +0/+2 and has first strike as long as an
 *   Equipment named Groom's Finery is attached to a creature you control.
 *   Equip {2}
 *
 * Exercises the unconditional +2/+0 static plus the two conditional statics (additional +0/+2 and
 * first strike) that switch on continuously only while a Groom's Finery is attached to a creature
 * the Gown's controller controls.
 */
class BridesGownScenarioTest : ScenarioTestBase() {

    init {
        context("Bride's Gown — conditional bonus keyed off Groom's Finery") {

            test("without Groom's Finery, the equipped creature is only +2/+0 and lacks first strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Bride's Gown", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 2/0 = 4/2") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
                withClue("no first strike without Groom's Finery") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe false
                }
            }

            test("with a Groom's Finery attached to a creature you control, the Gown grants +0/+2 and first strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Bride's Gown", "Grizzly Bears")
                    .withCardAttachedTo(1, "Groom's Finery", "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 2/0 + 0/2 = 4/4") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 4
                }
                withClue("the Gown-equipped creature has first strike") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
                }
            }

            test("a Groom's Finery attached to an OPPONENT's creature does not switch on the bonus") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Bride's Gown", "Grizzly Bears")
                    .withCardAttachedTo(2, "Groom's Finery", "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Finery is on an opponent's creature, so only the base +2/+0 applies (4/2)") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
                withClue("no first strike — the condition requires a creature YOU control") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe false
                }
            }
        }
    }
}
