package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Hardened Tactician (Tarkir: Dragonstorm #191).
 *
 * Card reference:
 * - Hardened Tactician ({1}{W}{B}): Creature — Human Warrior, 2/4
 *   "{1}, Sacrifice a token: Draw a card."
 *
 * These tests verify the token-only sacrifice cost (engine gap item 20): the cost is the
 * existing generic sacrifice cost narrowed by `GameObjectFilter.Token`, so only a token can
 * pay it. A nontoken permanent must not satisfy the cost.
 */
class HardenedTacticianScenarioTest : ScenarioTestBase() {

    init {
        context("Hardened Tactician — {1}, Sacrifice a token: Draw a card") {

            test("sacrificing a token pays the cost and draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hardened Tactician")
                    .withCardOnBattlefield(1, "Treasure", isToken = true)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tactician = game.findPermanent("Hardened Tactician")!!
                val token = game.findPermanent("Treasure")!!
                val abilityId = cardRegistry.getCard("Hardened Tactician")!!
                    .script.activatedAbilities.first().id

                val handBefore = game.handSize(1)

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = tactician,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(token))
                    )
                )
                withClue("Activating the draw ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                // Sacrifice is paid immediately as a cost; the draw resolves off the stack.
                withClue("The sacrificed token should be gone from the battlefield") {
                    game.findPermanent("Treasure") shouldBe null
                }

                game.resolveStack()

                withClue("Player should have drawn exactly one card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("the ability cannot be activated without a token to sacrifice") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hardened Tactician")
                    // A nontoken creature is present, but the cost demands a *token*.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tactician = game.findPermanent("Hardened Tactician")!!

                val drawAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == tactician
                }
                // The engine still surfaces the ability so the player can see it, but it must be
                // greyed-out (unaffordable) — there's no token to pay the sacrifice cost (CR 602.2b).
                withClue("Hardened Tactician's draw ability must not be affordable with no token to sacrifice") {
                    drawAction?.isAffordable shouldBe false
                }
            }

            test("a nontoken creature cannot be used to pay the token sacrifice cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hardened Tactician")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tactician = game.findPermanent("Hardened Tactician")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val abilityId = cardRegistry.getCard("Hardened Tactician")!!
                    .script.activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = tactician,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bears))
                    )
                )
                withClue("Sacrificing a nontoken creature must not satisfy 'Sacrifice a token'") {
                    result.error shouldNotBe null
                }
                withClue("Grizzly Bears should still be on the battlefield (cost rejected)") {
                    game.findPermanent("Grizzly Bears") shouldBe bears
                }
            }
        }
    }
}
