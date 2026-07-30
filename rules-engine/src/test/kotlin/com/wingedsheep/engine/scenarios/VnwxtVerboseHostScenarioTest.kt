package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.speed.SpeedService
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Speed
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vnwxt, Verbose Host (DFT #73) — {1}{U} Legendary Creature — Homunculus, 0/4.
 *
 * "Start your engines!
 *  You have no maximum hand size.
 *  Max speed — If you would draw a card, draw two cards instead."
 *
 * Exercises the `multiplier` knob added to [com.wingedsheep.sdk.scripting.ModifyDrawAmount], and
 * the max-speed gate folded into that effect's own `restrictions` slot (replacement effects can't
 * go through `maxSpeed { }`). The two things worth proving:
 *
 * - the doubling applies to the *announced* count, so Harmonize's "draw three cards" draws six —
 *   the literal example in the card's rulings, and the reason this is a multiplier and not a `+1`;
 * - below max speed the effect does nothing at all, at every announcement site.
 */
class VnwxtVerboseHostScenarioTest : ScenarioTestBase() {

    init {
        context("Vnwxt's max-speed draw doubling") {

            test("Harmonize's draw three becomes draw six at max speed") {
                val game = vnwxtGame(maxSpeed = true, libraryCards = 20)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                val handBefore = game.handSize(1)

                val cast = game.castSpell(1, "Harmonize")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.payManaIfAsked()
                game.resolveStack()

                // Harmonize leaves hand as it's cast, so the delta is drawn cards only.
                withClue("Draw 3 × 2 = 6, not 3 + 1 = 4") {
                    game.handSize(1) - (handBefore - 1) shouldBe 6
                }
            }

            test("Harmonize draws its printed three below max speed") {
                val game = vnwxtGame(maxSpeed = false, libraryCards = 20)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                val handBefore = game.handSize(1)

                game.castSpell(1, "Harmonize").error shouldBe null
                game.payManaIfAsked()
                game.resolveStack()

                withClue("Start your engines! only sets speed 1 — the gate must hold") {
                    game.handSize(1) - (handBefore - 1) shouldBe 3
                }
            }

            test("the draw step draws two at max speed") {
                // The second announcement site: DrawPhaseManager.performDrawStep. Turn 2 so the
                // step isn't skipped for the starting player.
                val game = vnwxtGame(maxSpeed = true, libraryCards = 20, turnNumber = 2)
                val handBefore = game.handSize(1)

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                withClue("Draw step's draw 1 doubles to 2") {
                    game.handSize(1) - handBefore shouldBe 2
                }
            }

            test("the draw step draws one below max speed") {
                val game = vnwxtGame(maxSpeed = false, libraryCards = 20, turnNumber = 2)
                val handBefore = game.handSize(1)

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                withClue("Draw step's draw 1 stays 1") {
                    game.handSize(1) - handBefore shouldBe 1
                }
            }
        }
    }

    /** Clear the mana-source prompt when casting Harmonize opens one. */
    private fun TestGame.payManaIfAsked() {
        if (getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
            submitManaSourcesAutoPay()
        }
    }

    /**
     * Vnwxt already on the battlefield with enough Forests for Harmonize ({3}{G}{G}) plus the
     * Island for Vnwxt's own colour, and a stocked library so a doubled draw can't deck anyone.
     * Speed is stamped directly rather than raced up through opponent life loss — the state-based
     * action for Start your engines! has already put the controller at 1 by then.
     */
    private fun vnwxtGame(
        maxSpeed: Boolean,
        libraryCards: Int,
        turnNumber: Int = 1
    ): TestGame {
        val builder = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Vnwxt, Verbose Host", summoningSickness = false)
            .withCardInHand(1, "Harmonize")
            .withLandsOnBattlefield(1, "Forest", 5)
        repeat(libraryCards) {
            builder.withCardInLibrary(1, "Grizzly Bears")
            builder.withCardInLibrary(2, "Grizzly Bears")
        }
        val game = builder
            .withActivePlayer(1)
            .withTurnNumber(turnNumber)
            .inPhase(Phase.BEGINNING, Step.UPKEEP)
            .build()
        if (maxSpeed) {
            game.state = SpeedService.set(game.state, game.player1Id, Speed.MAX, "test").first
        }
        return game
    }
}
