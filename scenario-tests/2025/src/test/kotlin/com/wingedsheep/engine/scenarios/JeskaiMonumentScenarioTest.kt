package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Jeskai Monument (TDM #244).
 *
 * "When this artifact enters, search your library for a basic Island, Mountain, or Plains card,
 *  reveal it, put it into your hand, then shuffle."
 *
 * Verifies the ETB search filter: a basic Island / Mountain / Plains is a legal choice and lands in
 * hand, while a basic Forest (not one of the three named types) is not offered.
 */
class JeskaiMonumentScenarioTest : ScenarioTestBase() {

    init {
        context("Jeskai Monument") {

            test("ETB search fetches a basic Island, Mountain, or Plains into hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Jeskai Monument")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Jeskai Monument")
                withClue("Casting Jeskai Monument should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val decision = game.getPendingDecision()
                withClue("ETB should prompt a library search") {
                    (decision is SelectCardsDecision) shouldBe true
                }
                val options = (decision as SelectCardsDecision).options
                withClue("Only the basic Island should be a legal choice (Forest is excluded)") {
                    options.mapNotNull { game.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name }
                        .toSet() shouldBe setOf("Island")
                }

                game.selectCards(listOf(options.first()))
                game.resolveStack()

                withClue("Jeskai Monument should be on the battlefield") {
                    game.isOnBattlefield("Jeskai Monument") shouldBe true
                }
                withClue("The Island should now be in hand") {
                    game.findCardsInHand(1, "Island").size shouldBe 1
                }
                withClue("The Island should no longer be in the library") {
                    game.findCardsInLibrary(1, "Island").size shouldBe 0
                }
            }
        }
    }
}
