package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Harrier Strix (OTJ) — {U} Bird 1/1, Flying.
 *
 * "When this creature enters, tap target permanent."
 * "{2}{U}: Draw a card, then discard a card."
 */
class HarrierStrixScenarioTest : ScenarioTestBase() {

    private val lootAbilityId =
        cardRegistry.getCard("Harrier Strix")!!.activatedAbilities.first().id

    init {
        context("Harrier Strix abilities") {

            test("ETB taps target permanent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Harrier Strix")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardOnBattlefield(2, "Centaur Courser", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Centaur Courser")!!
                val cast = game.castSpell(1, "Harrier Strix")
                withClue("casting Harrier Strix should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB trigger asks for a target permanent.
                game.selectTargets(listOf(target))
                game.resolveStack()

                withClue("the targeted permanent is tapped") {
                    game.state.getEntity(target)?.has<TappedComponent>() shouldBe true
                }
            }

            test("loot ability draws then discards (net hand size unchanged)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Harrier Strix", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(1, "Forest")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val strix = game.findPermanent("Harrier Strix")!!
                val handBefore = game.handSize(1)

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = strix,
                        abilityId = lootAbilityId
                    )
                )
                withClue("activating loot should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                // Draw a card (hand +1), then a discard decision is pending.
                val toDiscard = game.findCardsInHand(1, "Forest").first()
                game.selectCards(listOf(toDiscard))
                game.resolveStack()

                withClue("draw a card, then discard a card -> net hand size unchanged") {
                    game.handSize(1) shouldBe handBefore
                }
                withClue("the discarded card is in the graveyard") {
                    game.isInGraveyard(1, "Forest") shouldBe true
                }
            }
        }
    }
}
