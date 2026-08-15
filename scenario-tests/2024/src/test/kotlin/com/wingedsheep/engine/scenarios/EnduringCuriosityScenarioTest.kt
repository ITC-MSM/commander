package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Enduring Curiosity.
 *
 * Enduring Curiosity ({2}{U}{U}): Enchantment Creature — Cat Glimmer, 4/3
 * - Flash
 * - Whenever a creature you control deals combat damage to a player, draw a card.
 * - Enduring: when it dies (as a creature) it returns as a (non-creature) enchantment.
 *
 * The Enduring death clause is covered by EnduringMechanicTest; here we prove the
 * unique combat-damage draw trigger, including that it counts *any* creature you control
 * (not just Curiosity itself) and that an opponent's creature does not trigger it.
 */
class EnduringCuriosityScenarioTest : ScenarioTestBase() {

    init {
        context("Enduring Curiosity's combat-damage draw trigger") {

            test("a creature you control dealing combat damage to a player draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Curiosity", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val handBefore = game.handSize(1)

                // Attack with the (other) Grizzly Bears — an unblocked creature you control.
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.state.pendingDecision != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("Combat damage to the player should have triggered a draw") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("Enduring Curiosity itself dealing combat damage to a player draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Curiosity", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val handBefore = game.handSize(1)

                game.declareAttackers(mapOf("Enduring Curiosity" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.state.pendingDecision != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("Curiosity is also a creature you control, so its own combat damage draws") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("an opponent's creature dealing combat damage does NOT draw you a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Enduring Curiosity", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val handBefore = game.handSize(1)

                // Player 2 attacks Player 1 with their own creature.
                game.declareAttackers(mapOf("Grizzly Bears" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.state.pendingDecision != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("The trigger only fires for a creature YOU control; no draw here") {
                    game.handSize(1) shouldBe handBefore
                }
            }
        }
    }
}
