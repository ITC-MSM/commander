package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Solitary Cell (FRA #149) — {R}{W} Artifact.
 *
 *   When this artifact enters, exile target nonland permanent an opponent controls with mana value
 *   3 or less until this artifact leaves the battlefield.
 *   {1}, {T}, Discard a legendary card: Draw a card.
 */
class SolitaryCellScenarioTest : ScenarioTestBase() {

    init {
        context("Solitary Cell") {

            test("exiles an opponent's mana value 3 or less permanent until the Cell leaves") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardInHand(1, "Solitary Cell")
                    .withCardInHand(1, "Shatter")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!
                val lions = game.findPermanent("Savannah Lions")!!

                game.castSpell(1, "Solitary Cell").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as ChooseTargetsDecision
                val legal = decision.legalTargets[0] ?: emptyList()
                withClue("only the opponent's mana value 3 or less nonland permanent is legal") {
                    legal shouldContain bears
                    legal shouldNotContain giant
                    legal shouldNotContain lions
                }
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("the Bears are exiled") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }

                val cell = game.findPermanent("Solitary Cell")!!
                game.castSpell(1, "Shatter", cell).error shouldBe null
                game.resolveStack()

                withClue("the Cell left, so the Bears return to their owner") {
                    game.isOnBattlefield("Solitary Cell") shouldBe false
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("{1}, {T}, discard a legendary card: draw a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardOnBattlefield(1, "Solitary Cell")
                    .withCardInHand(1, "Hapatra, the Desert Frost")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cell = game.findPermanent("Solitary Cell")!!
                val abilityId = cardRegistry.getCard("Solitary Cell")!!.activatedAbilities.first().id

                val hapatra = game.findCardsInHand(1, "Hapatra, the Desert Frost").single()
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = cell,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(hapatra)),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the legendary card was discarded to pay the cost") {
                    game.isInGraveyard(1, "Hapatra, the Desert Frost") shouldBe true
                }
                withClue("and a card was drawn") { game.isInHand(1, "Grizzly Bears") shouldBe true }
            }

            test("a nonlegendary card can't pay the discard cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withCardOnBattlefield(1, "Solitary Cell")
                    .withCardInHand(1, "Hill Giant")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cell = game.findPermanent("Solitary Cell")!!
                val abilityId = cardRegistry.getCard("Solitary Cell")!!.activatedAbilities.first().id

                val giant = game.findCardsInHand(1, "Hill Giant").single()
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = cell,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(giant)),
                    )
                ).error shouldNotBe null
                game.isInHand(1, "Hill Giant") shouldBe true
            }
        }
    }
}
