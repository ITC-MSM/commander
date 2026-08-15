package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Fleeting Spirit (VOW #14) — {1}{W} Creature — Spirit, 3/1.
 *
 *   {W}, Exile three cards from your graveyard: This creature gains first strike until end of turn.
 *   Discard a card: Exile this creature. Return it to the battlefield under its owner's control at
 *   the beginning of the next end step.
 *
 * Test 1 pays {W} + exiling three graveyard cards to grant itself first strike. Test 2 exercises the
 * self-blink: discard a card to exile Fleeting Spirit now, then confirm the delayed trigger returns
 * it to the battlefield at the next end step.
 */
class FleetingSpiritScenarioTest : ScenarioTestBase() {

    init {
        context("Fleeting Spirit") {

            test("{W}, exile three from graveyard: gains first strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Fleeting Spirit", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 1) // pays {W}
                    // Three fodder cards in the graveyard to exile as the additional cost.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Savannah Lions")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spirit = game.findPermanent("Fleeting Spirit")!!
                val firstStrikeAbility = cardRegistry.getCard("Fleeting Spirit")!!
                    .activatedAbilities[0].id
                val toExile = game.findCardsInGraveyard(1, "Grizzly Bears") +
                    game.findCardsInGraveyard(1, "Savannah Lions") +
                    game.findCardsInGraveyard(1, "Lightning Bolt")

                withClue("Fleeting Spirit has no first strike before activation") {
                    game.state.projectedState.hasKeyword(spirit, Keyword.FIRST_STRIKE) shouldBe false
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = spirit,
                        abilityId = firstStrikeAbility,
                        costPayment = AdditionalCostPayment(exiledCards = toExile),
                    )
                )
                withClue("Activating the first-strike ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("all three graveyard cards were exiled as the cost") {
                    game.graveyardSize(1) shouldBe 0
                    game.isInExile(1, "Grizzly Bears") shouldBe true
                    game.isInExile(1, "Savannah Lions") shouldBe true
                    game.isInExile(1, "Lightning Bolt") shouldBe true
                }
                withClue("Fleeting Spirit gains first strike until end of turn") {
                    game.state.projectedState.hasKeyword(spirit, Keyword.FIRST_STRIKE) shouldBe true
                }
            }

            test("Discard a card: exile Fleeting Spirit, then it returns at the next end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Fleeting Spirit", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears") // the card to discard
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spirit = game.findPermanent("Fleeting Spirit")!!
                val blinkAbility = cardRegistry.getCard("Fleeting Spirit")!!
                    .activatedAbilities[1].id
                val toDiscard = game.findCardsInHand(1, "Grizzly Bears").single()

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = spirit,
                        abilityId = blinkAbility,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(toDiscard)),
                    )
                )
                withClue("Activating the self-blink ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("the discarded card is in the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("Fleeting Spirit is exiled by its own ability") {
                    game.isOnBattlefield("Fleeting Spirit") shouldBe false
                    game.isInExile(1, "Fleeting Spirit") shouldBe true
                }

                // Advance to the end step: the delayed trigger returns Fleeting Spirit.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("at the next end step Fleeting Spirit returns to the battlefield") {
                    game.isOnBattlefield("Fleeting Spirit") shouldBe true
                    game.isInExile(1, "Fleeting Spirit") shouldBe false
                }
            }
        }
    }
}
