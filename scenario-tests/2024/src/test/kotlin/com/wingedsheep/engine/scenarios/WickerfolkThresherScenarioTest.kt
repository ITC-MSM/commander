package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Wickerfolk Thresher (DSK #207) — {3}{G} 5/4 Artifact Creature — Scarecrow.
 *
 * "Delirium — Whenever this creature attacks, if there are four or more card types among cards in
 *  your graveyard, look at the top card of your library. If it's a land card, you may put it onto
 *  the battlefield. If you don't put the card onto the battlefield, put it into your hand."
 *
 * Delirium gates the attack trigger as an intervening-"if" (four+ distinct card types in the
 * controller's graveyard). The payoff is the look-top / play-land-else-hand pipeline.
 */
class WickerfolkThresherScenarioTest : ScenarioTestBase() {

    init {
        context("Wickerfolk Thresher — delirium attack trigger") {

            // Four card types in graveyard: artifact, creature, instant, sorcery, land → ≥ 4.
            fun deliriumGraveyard(builder: ScenarioBuilder): ScenarioBuilder = builder
                .withCardInGraveyard(1, "Ornithopter")     // Artifact
                .withCardInGraveyard(1, "Grizzly Bears")   // Creature
                .withCardInGraveyard(1, "Lightning Bolt")  // Instant
                .withCardInGraveyard(1, "Forest")          // Land

            test("with delirium and a land on top, may put it onto the battlefield") {
                val game = deliriumGraveyard(
                    scenario()
                        .withPlayers("Player1", "Player2")
                        .withCardOnBattlefield(1, "Wickerfolk Thresher", tapped = false, summoningSickness = false)
                        .withCardInLibrary(1, "Mountain") // top of library is a land
                        .withCardInLibrary(2, "Island")
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                )
                    .build()

                val landsBefore = game.findPermanents("Mountain").size

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Wickerfolk Thresher" to 2))

                // Attack trigger goes on the stack; resolve until the look-top selection surfaces.
                var guard = 0
                while (game.getPendingDecision() !is SelectCardsDecision && guard++ < 20) {
                    game.resolveStack()
                }
                val decision = game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected a SelectCardsDecision for the land choice; got ${game.getPendingDecision()}")

                // Choose to put the land onto the battlefield.
                game.selectCards(listOf(decision.options.first()))
                game.resolveStack()

                withClue("The land entered the battlefield") {
                    game.findPermanents("Mountain").size shouldBe landsBefore + 1
                }
                withClue("Nothing went to hand") {
                    game.isInHand(1, "Mountain") shouldBe false
                }
            }

            test("with delirium and a land on top, declining puts it into your hand") {
                val game = deliriumGraveyard(
                    scenario()
                        .withPlayers("Player1", "Player2")
                        .withCardOnBattlefield(1, "Wickerfolk Thresher", tapped = false, summoningSickness = false)
                        .withCardInLibrary(1, "Mountain")
                        .withCardInLibrary(2, "Island")
                        .withActivePlayer(1)
                        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                )
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Wickerfolk Thresher" to 2))

                var guard = 0
                while (game.getPendingDecision() !is SelectCardsDecision && guard++ < 20) {
                    game.resolveStack()
                }
                game.getPendingDecision() as? SelectCardsDecision
                    ?: error("expected a SelectCardsDecision; got ${game.getPendingDecision()}")

                // Decline (select none) — the land goes to hand.
                game.selectCards(emptyList())
                game.resolveStack()

                withClue("Declining puts the land into hand") {
                    game.findPermanents("Mountain").size shouldBe 0
                    game.isInHand(1, "Mountain") shouldBe true
                }
            }

            test("without delirium, the attack trigger does nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wickerfolk Thresher", tapped = false, summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears") // only one card type
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Wickerfolk Thresher" to 2))
                game.resolveStack()

                withClue("No delirium → no look-top, no decision") {
                    (game.getPendingDecision() is SelectCardsDecision) shouldBe false
                    game.findPermanents("Mountain").size shouldBe 0
                    game.isInHand(1, "Mountain") shouldBe false
                }
            }
        }
    }
}
