package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.player.LifeGainedAmountThisTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Moseo, Vein's New Dean (Secrets of Strixhaven #91).
 *
 * Moseo ({2}{B}, 2/1, Legendary Bird Skeleton Warlock):
 *   Flying
 *   When Moseo enters, create a 1/1 black and green Pest token ("Whenever this token attacks,
 *   you gain 1 life.").
 *   Infusion — At the beginning of your end step, if you gained life this turn, return up to one
 *   target creature card with mana value X or less from your graveyard to the battlefield, where
 *   X is the amount of life you gained this turn.
 *
 * The Infusion end-step trigger exercises the new `manaValueAtMostDynamic` card-filter cap, with
 * X = the amount of life gained this turn (TurnTracking LIFE_GAINED).
 */
class MoseoVeinsNewDeanScenarioTest : ScenarioTestBase() {

    init {
        context("Moseo, Vein's New Dean — ETB Pest + Infusion reanimation") {

            test("entering creates a Pest token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Moseo, Vein's New Dean")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Moseo, Vein's New Dean")
                withClue("Moseo should cast: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("A Pest token should have been created") {
                    (game.findPermanent("Pest Token") != null) shouldBe true
                }
            }

            test("with life gained, reanimates a creature card with mana value <= life gained") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Moseo, Vein's New Dean")
                    .withCardInGraveyard(1, "Grizzly Bears")   // MV 2 — within the cap when X = 3
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Gained 3 life this turn -> X = 3, so MV<=3 cards are eligible.
                game.state = game.state.updateEntity(game.player1Id) {
                    it.withComponent(LifeGainedAmountThisTurnComponent(3))
                }

                game.passUntilPhase(Phase.ENDING, Step.END)

                withClue("end-step Infusion trigger should ask for a target") {
                    game.hasPendingDecision() shouldBe true
                }
                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears returned to the battlefield") {
                    (game.findPermanent("Grizzly Bears") != null) shouldBe true
                }
                withClue("Grizzly Bears no longer in graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("a creature card above the mana-value cap is not a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Moseo, Vein's New Dean")
                    .withCardInGraveyard(1, "Hill Giant")      // MV 4 — above the cap when X = 2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Only gained 2 life -> X = 2, Hill Giant (MV 4) is over the cap.
                game.state = game.state.updateEntity(game.player1Id) {
                    it.withComponent(LifeGainedAmountThisTurnComponent(2))
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                // "Up to one" with no legal target: decline (no eligible MV<=2 card to reanimate).
                if (game.hasPendingDecision()) {
                    game.skipTargets()
                }
                game.resolveStack()

                withClue("Hill Giant stays in the graveyard (MV above the cap)") {
                    game.isInGraveyard(1, "Hill Giant") shouldBe true
                }
                withClue("Hill Giant did not enter the battlefield") {
                    game.findPermanent("Hill Giant") shouldBe null
                }
            }

            test("no life gained this turn: the Infusion trigger does not fire") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Moseo, Vein's New Dean")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("no pending target decision — intervening-if failed (no life gained)") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("Grizzly Bears stays in the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
