package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Dina's Guidance {1}{B}{G} Instant — Secrets of Strixhaven #184.
 *
 * "Search your library for a creature card, reveal it, put it into your hand or graveyard, then
 *  shuffle."
 *
 * Exercises the gather → choose (search) → reveal → split-move (hand vs graveyard) pipeline.
 */
class DinasGuidanceScenarioTest : ScenarioTestBase() {

    init {
        context("Dina's Guidance — search to hand or graveyard") {

            test("found creature can be put into hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Dina's Guidance")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findCardsInLibrary(1, "Grizzly Bears").first()

                game.castSpell(1, "Dina's Guidance").error shouldBe null
                game.resolveStack()

                // 1. Search: pick the creature card.
                withClue("search prompt for the creature card") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(bears))
                game.resolveStack()

                // 2. Zone split: select the card → it goes to hand.
                withClue("zone choice prompt") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears is now in hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("not in graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("found creature can be put into graveyard (decline the zone selection)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Dina's Guidance")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findCardsInLibrary(1, "Grizzly Bears").first()

                game.castSpell(1, "Dina's Guidance").error shouldBe null
                game.resolveStack()

                // 1. Search: pick the creature card.
                game.hasPendingDecision() shouldBe true
                game.selectCards(listOf(bears))
                game.resolveStack()

                // 2. Zone split: select NOTHing → the card falls to the remainder (graveyard).
                game.hasPendingDecision() shouldBe true
                game.skipSelection()
                game.resolveStack()

                withClue("Grizzly Bears is now in graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("not in hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
            }

            test("search may fail to find: no creature card in library") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Dina's Guidance")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Swamp")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Dina's Guidance").error shouldBe null
                game.resolveStack()
                // No creature to find — resolve to completion without error.
                if (game.hasPendingDecision()) game.skipSelection()
                game.resolveStack()

                withClue("Dina's Guidance is in the graveyard after resolving") {
                    game.isInGraveyard(1, "Dina's Guidance") shouldBe true
                }
            }
        }
    }
}
