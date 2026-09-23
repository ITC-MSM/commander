package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Twinned Vision (FRA #157) — draws one from hand, two when it wasn't cast from hand; flashback
 * costs {1}{U/R}{U/R} plus a discard.
 */
class TwinnedVisionScenarioTest : ScenarioTestBase() {

    init {
        fun builder(): ScenarioBuilder {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            repeat(5) { b = b.withCardInLibrary(1, "Island") }
            return b
        }

        context("Twinned Vision") {
            test("cast from hand, it draws one card") {
                val game = builder().withCardInHand(1, "Twinned Vision").build()

                game.castSpell(1, "Twinned Vision").error shouldBe null
                game.resolveStack()

                game.handSize(1) shouldBe 1
                game.isInGraveyard(1, "Twinned Vision") shouldBe true
            }

            test("flashed back with a discard, it draws two cards and is exiled") {
                val game = builder()
                    .withCardInGraveyard(1, "Twinned Vision")
                    .withCardInHand(1, "Lightning Bolt")
                    .build()
                val vision = game.findCardsInGraveyard(1, "Twinned Vision").single()
                val bolt = game.findCardsInHand(1, "Lightning Bolt").single()

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = vision,
                        useAlternativeCost = true,
                        alternativeCostType = AlternativeCostType.FLASHBACK,
                        additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(bolt)),
                    )
                )
                withClue("flashback with a discard: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("the discarded Bolt paid the cost") { game.isInGraveyard(1, "Lightning Bolt") shouldBe true }
                game.handSize(1) shouldBe 2
                game.isInExile(1, "Twinned Vision") shouldBe true
            }
        }
    }
}
