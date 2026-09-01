package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Shepherd of the Cosmos (KHM #28) — {4}{W}{W} Creature — Angel Warrior, 3/3.
 *
 *   Flying
 *   When this creature enters, return target permanent card with mana value 2 or less from your
 *   graveyard to the battlefield.
 *   Foretell {3}{W}
 *
 * The trigger's target lives in the *graveyard*, so both halves have to name that zone: the target
 * requirement is graveyard-scoped, and so is the move's `fromZone`. Getting the second one wrong is
 * silent — the move looks the target up on the battlefield, finds nothing, and the trigger resolves
 * as a no-op with no error anywhere. These tests pin the card actually returning the permanent, and
 * the mana-value cap being enforced as a targeting restriction rather than on resolution.
 */
class ShepherdOfTheCosmosScenarioTest : ScenarioTestBase() {

    init {
        context("the enters trigger") {

            test("returns a mana value 2 permanent card from your graveyard to the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Shepherd of the Cosmos")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Shepherd of the Cosmos")
                withClue("Casting the Shepherd should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack() // the Shepherd enters; its trigger pauses for a target

                withClue("The enters trigger should pause for target selection") {
                    game.hasPendingDecision() shouldBe true
                }
                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                val select = game.selectTargets(listOf(bears))
                withClue("Grizzly Bears ({1}{G}) is within the mana value cap: ${select.error}") {
                    select.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears left the graveyard for the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("a mana value 3 permanent card is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Shepherd of the Cosmos")
                    .withCardInGraveyard(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Shepherd of the Cosmos")
                game.resolveStack()

                val giant = game.findCardsInGraveyard(1, "Hill Giant").single()
                if (game.hasPendingDecision()) {
                    withClue("Hill Giant ({3}{R}) is above the cap, so it is not a legal target") {
                        game.selectTargets(listOf(giant)).error shouldNotBe null
                    }
                    game.skipTargets()
                }
                game.resolveStack()

                withClue("Hill Giant costs {3}{R} — above the cap, so it stays in the graveyard") {
                    game.isInGraveyard(1, "Hill Giant") shouldBe true
                    game.isOnBattlefield("Hill Giant") shouldBe false
                }
                withClue("The Shepherd itself still resolved") {
                    game.isOnBattlefield("Shepherd of the Cosmos") shouldBe true
                }
            }
        }
    }
}
