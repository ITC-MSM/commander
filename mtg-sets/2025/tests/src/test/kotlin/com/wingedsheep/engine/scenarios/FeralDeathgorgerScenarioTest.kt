package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Feral Deathgorger // Dusk Sight (TDM #80).
 *
 * Feral Deathgorger — {5}{B} Dragon, 3/5, Flying, deathtouch.
 *   "When this creature enters, exile up to two target cards from a single graveyard."
 * Dusk Sight — {1}{B} Sorcery — Omen.
 *   "Put a +1/+1 counter on up to one target creature. Draw a card."
 *
 * The single-graveyard ETB reuses Arashin Sunshield's `TargetObject.sameOwner` path
 * (covered in depth there); here we confirm the creature face wires up that exile and
 * the Omen face applies the counter + draw.
 */
class FeralDeathgorgerScenarioTest : ScenarioTestBase() {

    init {
        context("Feral Deathgorger creature face") {

            test("ETB exiles up to two cards from a single graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Feral Deathgorger")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardInGraveyard(2, "Glory Seeker")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Feral Deathgorger").error shouldBe null
                game.resolveStack() // creature enters → ETB trigger asks for targets

                val t1 = game.findCardsInGraveyard(2, "Glory Seeker").first()
                val t2 = game.findCardsInGraveyard(2, "Hill Giant").first()
                val result = game.selectTargets(listOf(t1, t2))
                withClue("Two cards from the same graveyard is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Both targeted cards leave the graveyard") {
                    game.graveyardSize(2) shouldBe 0
                }
                withClue("Both cards are exiled") {
                    game.state.getExile(game.player2Id).size shouldBe 2
                }
                withClue("Feral Deathgorger is on the battlefield") {
                    game.isOnBattlefield("Feral Deathgorger") shouldBe true
                }
            }
        }

        context("Dusk Sight Omen face") {

            test("puts a +1/+1 counter on a target creature and draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Feral Deathgorger")
                    .withCardOnBattlefield(1, "Glory Seeker") // 2/2
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val creature = game.findPermanent("Glory Seeker")!!
                val handBefore = game.state.getHand(game.player1Id).size

                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Feral Deathgorger"
                }

                // Cast the Omen face (faceIndex = 0), targeting Glory Seeker.
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(ChosenTarget.Permanent(creature)),
                        faceIndex = 0,
                    )
                )
                withClue("Casting Dusk Sight should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Glory Seeker gets a +1/+1 counter") {
                    val counters = game.state.getEntity(creature)
                        ?.get<CountersComponent>()
                        ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 1
                }
                // Net hand: -1 (Feral Deathgorger cast) +1 (Dusk Sight draws) = unchanged.
                withClue("Dusk Sight draws a card") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore
                }
            }
        }
    }
}
