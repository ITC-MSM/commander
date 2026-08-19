package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Malevolent Rumble.
 *
 * Card reference:
 * - Malevolent Rumble ({1}{G}): Sorcery
 *   "Reveal the top four cards of your library. You may put a permanent card from among them
 *    into your hand. Put the rest into your graveyard. Create a 0/1 colorless Eldrazi Spawn
 *    creature token with 'Sacrifice this token: Add {C}.'"
 */
class MalevolentRumbleScenarioTest : ScenarioTestBase() {

    init {
        context("Malevolent Rumble") {
            test("puts the chosen permanent into hand, the rest into the graveyard, and creates a token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Malevolent Rumble")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInLibrary(1, "Grizzly Bears") // top — the only permanent
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.state.getZone(game.player1Id, Zone.LIBRARY)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }

                val cast = game.castSpell(1, "Malevolent Rumble")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }

                game.resolveStack()
                game.selectCards(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears should be put into hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("The three non-permanent cards should be in the graveyard") {
                    game.isInGraveyard(1, "Mountain") shouldBe true
                    game.isInGraveyard(1, "Island") shouldBe true
                    game.isInGraveyard(1, "Swamp") shouldBe true
                }
                withClue("An Eldrazi Spawn token should be created") {
                    game.findPermanents("Eldrazi Spawn") shouldHaveSize 1
                }
            }

            test("declining the optional pick puts all four cards into the graveyard, still creates a token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Malevolent Rumble")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Malevolent Rumble")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }

                game.resolveStack()
                game.skipSelection()
                game.resolveStack()

                withClue("Nothing was put into hand") {
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
                withClue("All four revealed cards plus Malevolent Rumble itself are in the graveyard") {
                    game.graveyardSize(1) shouldBe 5
                }
                withClue("An Eldrazi Spawn token should still be created") {
                    game.findPermanents("Eldrazi Spawn") shouldHaveSize 1
                }
            }
        }
    }
}
