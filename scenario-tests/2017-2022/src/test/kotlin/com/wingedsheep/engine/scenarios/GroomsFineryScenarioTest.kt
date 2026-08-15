package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Groom's Finery (VOW #117) — {1}{B} Artifact — Equipment
 *
 *   Equipped creature gets +2/+0. It gets an additional +0/+2 and has deathtouch as long as an
 *   Equipment named Bride's Gown is attached to a creature you control.
 *   Equip {2}
 *
 * Mirror of Bride's Gown: the conditional half grants deathtouch (not first strike) and keys off a
 * Bride's Gown attached to a creature the Finery's controller controls.
 */
class GroomsFineryScenarioTest : ScenarioTestBase() {

    init {
        context("Groom's Finery — conditional bonus keyed off Bride's Gown") {

            test("without Bride's Gown, the equipped creature is only +2/+0 and lacks deathtouch") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Groom's Finery", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 2/0 = 4/2") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
                withClue("no deathtouch without Bride's Gown") {
                    game.state.projectedState.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe false
                }
            }

            test("with a Bride's Gown attached to a creature you control, the Finery grants +0/+2 and deathtouch") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Groom's Finery", "Grizzly Bears")
                    .withCardAttachedTo(1, "Bride's Gown", "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 2/0 + 0/2 = 4/4") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 4
                }
                withClue("the Finery-equipped creature has deathtouch") {
                    game.state.projectedState.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe true
                }
            }

            test("a Bride's Gown attached to an OPPONENT's creature does not switch on the bonus") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", tapped = false, summoningSickness = false)
                    .withCardAttachedTo(1, "Groom's Finery", "Grizzly Bears")
                    .withCardAttachedTo(2, "Bride's Gown", "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Gown is on an opponent's creature, so only the base +2/+0 applies (4/2)") {
                    game.state.projectedState.getPower(bears) shouldBe 4
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
                withClue("no deathtouch — the condition requires a creature YOU control") {
                    game.state.projectedState.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe false
                }
            }
        }
    }
}
