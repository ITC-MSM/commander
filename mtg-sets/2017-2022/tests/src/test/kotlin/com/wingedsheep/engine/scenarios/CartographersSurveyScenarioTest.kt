package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Cartographer's Survey (VOW #190) — {3}{G} Sorcery
 *
 *   Look at the top seven cards of your library. Put up to two land cards from among them onto the
 *   battlefield tapped. Put the rest on the bottom of your library in a random order.
 *
 * Exercises the Gather → Select → Move library pipeline: the caster privately looks at the top
 * seven, may put up to two *land* cards onto the battlefield tapped, and the remainder is bottomed.
 */
class CartographersSurveyScenarioTest : ScenarioTestBase() {

    init {
        context("Cartographer's Survey — look at top seven, put up to two lands onto the battlefield tapped") {

            test("puts two chosen Forests onto the battlefield tapped and bottoms the rest") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cartographer's Survey")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    // Seven-card library: five Forests (land) + two Grizzly Bears (nonland).
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forestsBefore = game.findPermanents("Forest").size

                game.castSpell(1, "Cartographer's Survey").error shouldBe null
                game.resolveStack()

                withClue("resolution pauses for the up-to-two land selection") {
                    game.hasPendingDecision() shouldBe true
                }
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()

                withClue("only the five land cards are eligible options (Grizzly Bears filtered out)") {
                    decision.options.size shouldBe 5
                }

                // Choose two of the eligible land cards.
                val twoLands = decision.options.take(2)
                game.submitDecision(
                    CardsSelectedResponse(decisionId = decision.id, selectedCards = twoLands)
                ).error shouldBe null
                game.resolveStack()

                withClue("two more Forests are now on the battlefield") {
                    game.findPermanents("Forest").size shouldBe forestsBefore + 2
                }
                withClue("both newly placed lands entered tapped") {
                    twoLands.forEach { land ->
                        game.state.getEntity(land)?.get<CardComponent>()?.name shouldBe "Forest"
                        (game.state.getEntity(land)?.has<TappedComponent>() ?: false) shouldBe true
                    }
                }
                withClue("the remaining five looked-at cards were bottomed (library back to 5)") {
                    game.librarySize(1) shouldBe 5
                }
            }

            test("declining the selection bottoms all seven and puts nothing onto the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cartographer's Survey")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forestsBefore = game.findPermanents("Forest").size

                game.castSpell(1, "Cartographer's Survey").error shouldBe null
                game.resolveStack()

                game.hasPendingDecision() shouldBe true
                // "up to two" — decline by selecting none.
                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("no lands were put onto the battlefield") {
                    game.findPermanents("Forest").size shouldBe forestsBefore
                }
                withClue("all seven looked-at cards were bottomed (library back to 7)") {
                    game.librarySize(1) shouldBe 7
                }
            }
        }
    }
}
