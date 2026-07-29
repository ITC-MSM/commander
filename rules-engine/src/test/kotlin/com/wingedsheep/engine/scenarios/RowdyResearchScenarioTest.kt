package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rowdy Research (WOE #312).
 *
 *   {6}{U} Instant
 *   This spell costs {1} less to cast for each creature that attacked this turn.
 *   Draw three cards.
 *
 * The interesting half is the discount, and specifically that it is *turn history* rather than a
 * battlefield scan: this is an instant you cast after combat, when the attackers that paid for it
 * are frequently dead. The other guard is the colored pip — no number of attackers may reduce the
 * cost below {U}.
 */
class RowdyResearchScenarioTest : ScenarioTestBase() {

    init {
        context("Rowdy Research") {

            test("no creature attacked → the full 6 generic") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rowdy Research")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Rowdy Research"),
                    game.player1Id,
                )

                withClue("nothing attacked, so the generic component stays at 6") {
                    cost.genericAmount shouldBe 6
                }
            }

            test("two creatures attacked → 4 generic") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rowdy Research")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf("Grizzly Bears" to 2, "Hill Giant" to 2)
                ).error shouldBe null

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Rowdy Research"),
                    game.player1Id,
                )

                withClue("two attackers reduce the generic from 6 to 4") {
                    cost.genericAmount shouldBe 4
                }
            }

            test("an attacker that died in combat still discounts the spell") {
                // The card is an after-combat instant, so this is the case the turn-history
                // source exists for: a battlefield scan would un-discount the dead attacker.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rowdy Research")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears"))).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("the attacker traded and is no longer on the battlefield") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Rowdy Research"),
                    game.player1Id,
                )

                withClue("the dead attacker still counts, so 6 becomes 5") {
                    cost.genericAmount shouldBe 5
                }
            }

            test("the reduction never eats the {U}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rowdy Research")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(1, "Savannah Lions", summoningSickness = false)
                    .withCardOnBattlefield(1, "Air Elemental", summoningSickness = false)
                    .withCardOnBattlefield(1, "Craw Wurm", summoningSickness = false)
                    .withCardOnBattlefield(1, "Bog Imp", summoningSickness = false)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(
                    mapOf(
                        "Grizzly Bears" to 2,
                        "Hill Giant" to 2,
                        "Savannah Lions" to 2,
                        "Air Elemental" to 2,
                        "Craw Wurm" to 2,
                        "Bog Imp" to 2,
                    )
                ).error shouldBe null

                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Rowdy Research"),
                    game.player1Id,
                )

                withClue("six attackers clear all 6 generic but cannot reduce below {U}") {
                    cost.genericAmount shouldBe 0
                    cost.colorCount[Color.BLUE] shouldBe 1
                }
            }

            test("resolving Rowdy Research draws three cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rowdy Research")
                    .withLandsOnBattlefield(1, "Island", 7)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.castSpell(1, "Rowdy Research").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("the spell leaves hand (-1) and draws three (+3)") {
                    game.handSize(1) shouldBe handBefore - 1 + 3
                }
                withClue("all three library cards were drawn") {
                    game.librarySize(1) shouldBe 0
                }
            }
        }
    }
}
