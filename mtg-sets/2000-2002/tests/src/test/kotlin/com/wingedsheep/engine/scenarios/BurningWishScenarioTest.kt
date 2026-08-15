package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for the **wish** mechanic, exercised through Burning Wish.
 *
 * Burning Wish {1}{R} — Sorcery:
 * "You may reveal a sorcery card you own from outside the game and put it into your hand.
 *  Exile Burning Wish."
 *
 * "Outside the game" is modelled as the private [com.wingedsheep.sdk.core.Zone.SIDEBOARD]
 * (CR 100.4 / 400.11a). Each clause of the card is pinned by a test below:
 * - the fetch moves a chosen *sorcery* from the sideboard to hand, leaving the sideboard;
 * - the filter excludes non-sorcery sideboard cards (only sorceries are offered);
 * - "you may" — the controller can decline and fetch nothing;
 * - an empty/no-matching sideboard fetches nothing (no error);
 * - "Exile Burning Wish" — the spell is exiled on resolution, not put into the graveyard
 *   (CR 608.2g), in every branch.
 */
class BurningWishScenarioTest : ScenarioTestBase() {

    init {
        context("Burning Wish fetches a sorcery from the sideboard") {
            test("chosen sorcery goes to hand, leaves the sideboard, and Burning Wish is exiled") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Burning Wish")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInSideboard(1, "Swelter")        // a sorcery — wish-eligible
                    .withCardInSideboard(1, "Grizzly Bears")  // a creature — NOT eligible
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Burning Wish")
                withClue("Burning Wish should be castable: ${cast.error}") { cast.error shouldBe null }

                game.resolveStack()

                withClue("Should pause to choose a sorcery from the sideboard") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision() as? SelectCardsDecision
                decision.shouldNotBeNull()
                val offered = decision.cardInfo!!

                withClue("Only the sorcery should be offered — the creature is filtered out") {
                    offered.values.any { it.name == "Swelter" } shouldBe true
                    offered.values.any { it.name == "Grizzly Bears" } shouldBe false
                }

                val swelterId = offered.entries.first { it.value.name == "Swelter" }.key
                game.selectCards(listOf(swelterId))

                withClue("Swelter should now be in player 1's hand") {
                    game.isInHand(1, "Swelter") shouldBe true
                }
                withClue("Swelter should have left the sideboard") {
                    game.isInSideboard(1, "Swelter") shouldBe false
                }
                withClue("Grizzly Bears should remain in the sideboard (not fetched)") {
                    game.isInSideboard(1, "Grizzly Bears") shouldBe true
                }
                withClue("Burning Wish should be exiled, not in the graveyard (CR 608.2g)") {
                    game.isInExile(1, "Burning Wish") shouldBe true
                    game.isInGraveyard(1, "Burning Wish") shouldBe false
                }
            }
        }

        context("\"you may\" — declining fetches nothing") {
            test("skipping the choice leaves the sideboard intact and still exiles Burning Wish") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Burning Wish")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInSideboard(1, "Swelter")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Burning Wish")
                game.resolveStack()

                withClue("Should offer the optional sideboard choice") {
                    game.hasPendingDecision() shouldBe true
                }
                game.skipSelection()

                withClue("Swelter should stay in the sideboard when the wish is declined") {
                    game.isInSideboard(1, "Swelter") shouldBe true
                }
                withClue("Player 1 should not have fetched anything into hand") {
                    game.isInHand(1, "Swelter") shouldBe false
                }
                withClue("Burning Wish is still exiled even when nothing is fetched") {
                    game.isInExile(1, "Burning Wish") shouldBe true
                    game.isInGraveyard(1, "Burning Wish") shouldBe false
                }
            }
        }

        context("no eligible card in the sideboard") {
            test("a sideboard with no sorcery fetches nothing and still exiles Burning Wish") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Burning Wish")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInSideboard(1, "Grizzly Bears") // no sorcery anywhere in the sideboard
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Burning Wish")
                game.resolveStack()

                // With nothing eligible to gather, the choice may resolve with no decision at all;
                // if a (necessarily empty) decision is presented, decline it.
                if (game.hasPendingDecision()) game.skipSelection()

                withClue("Nothing should be fetched into hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
                withClue("The creature stays in the sideboard") {
                    game.isInSideboard(1, "Grizzly Bears") shouldBe true
                }
                withClue("Burning Wish is exiled regardless of whether a card was fetched") {
                    game.isInExile(1, "Burning Wish") shouldBe true
                    game.isInGraveyard(1, "Burning Wish") shouldBe false
                }
            }
        }
    }
}
