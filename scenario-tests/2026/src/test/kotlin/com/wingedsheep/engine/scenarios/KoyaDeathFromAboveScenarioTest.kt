package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Koya, Death from Above (TMT).
 *
 * "When Koya enters, exile up to one other target creature. At the beginning of the next end step,
 *  you may pay {3}{B}. If you don't, return that card to the battlefield under its owner's control."
 *
 * Regression guard for the return's controller: when Koya exiles an *opponent's* creature and the
 * {3}{B} isn't paid, the creature must come back under its **owner's** control (player 2), not under
 * Koya's controller's (player 1). The exile/return uses a linked exile with
 * `MoveCollectionEffect(underOwnersControl = true)`; without that flag the controller would default
 * to `Player.You` and Koya would steal the creature.
 */
class KoyaDeathFromAboveScenarioTest : ScenarioTestBase() {

    init {
        context("Koya, Death from Above end-step return") {

            test("an opponent's exiled creature returns under its owner's control when {3}{B} isn't paid") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Koya, Death from Above")
                    .withLandsOnBattlefield(1, "Plains", 3) // pays {2}{W}; no black source for {3}{B}
                    .withCardOnBattlefield(2, "Centaur Courser") // opponent's 3/3
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentCreature = game.findPermanent("Centaur Courser")
                    ?: error("Centaur Courser should start on player 2's battlefield")

                // Koya enters; its ETB trigger targets and exiles the opponent's creature.
                game.castSpell(1, "Koya, Death from Above")
                game.resolveStack()
                game.selectTargets(listOf(opponentCreature))
                game.resolveStack()

                withClue("Centaur Courser is exiled by Koya's ETB") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                }

                // Next end step: player 1 has no black mana, so {3}{B} can't be paid — the "if you
                // don't" branch returns the creature to the battlefield.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                val returned = game.findPermanent("Centaur Courser")
                    ?: error("Centaur Courser should have returned to the battlefield")
                withClue("Returned under its owner's (player 2's) control, not Koya's controller's") {
                    game.state.getEntity(returned)?.get<ControllerComponent>()?.playerId shouldBe game.player2Id
                }
            }
        }
    }
}
