package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Laughing Mad (FIN #143).
 *
 * Laughing Mad — {2}{R} Instant.
 *   "As an additional cost to cast this spell, discard a card.
 *    Draw two cards.
 *    Flashback {3}{R}"
 */
class LaughingMadScenarioTest : ScenarioTestBase() {

    init {
        test("discards a card as an additional cost and draws two cards") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Laughing Mad")
                .withCardInHand(1, "Forest") // the card to discard
                .withLandsOnBattlefield(1, "Mountain", 3)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .build()

            val laughingMad = game.findCardsInHand(1, "Laughing Mad").first()
            val discard = game.findCardsInHand(1, "Forest").first()

            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = laughingMad,
                    targets = emptyList(),
                    additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(discard))
                )
            ).error shouldBe null

            withClue("the discarded card is in the graveyard") {
                game.isInGraveyard(1, "Forest") shouldBe true
            }

            game.resolveStack()

            withClue("two cards were drawn") {
                game.isInHand(1, "Plains") shouldBe true
                game.isInHand(1, "Island") shouldBe true
            }
        }

        test("can be cast from the graveyard with flashback, then is exiled") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInGraveyard(1, "Laughing Mad")
                .withCardInHand(1, "Forest") // discarded for the additional cost
                .withLandsOnBattlefield(1, "Mountain", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .build()

            val laughingMad = game.findCardsInGraveyard(1, "Laughing Mad").first()
            val discard = game.findCardsInHand(1, "Forest").first()

            game.execute(
                // Casting from the graveyard auto-applies the flashback alternative cost; the
                // discard additional cost still applies ("its flashback cost and any additional costs").
                CastSpell(
                    playerId = game.player1Id,
                    cardId = laughingMad,
                    targets = emptyList(),
                    additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(discard))
                )
            ).error shouldBe null

            game.resolveStack()

            withClue("flashback exiles the card after it resolves") {
                game.isInExile(1, "Laughing Mad") shouldBe true
                game.isInGraveyard(1, "Laughing Mad") shouldBe false
            }
            withClue("two cards were drawn from the flashback cast") {
                game.isInHand(1, "Plains") shouldBe true
                game.isInHand(1, "Island") shouldBe true
            }
        }
    }
}
