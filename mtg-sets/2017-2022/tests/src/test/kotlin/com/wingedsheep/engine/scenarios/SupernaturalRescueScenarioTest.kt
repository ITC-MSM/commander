package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Supernatural Rescue (VOW #37) — {3}{W} Enchantment — Aura.
 *
 *   This spell has flash as long as you control a Spirit.
 *   When you cast this spell, tap up to two target creatures you don't control.
 *   Enchant creature you control
 *   Enchanted creature gets +1/+2.
 *
 * Three separate things to pin: the conditional flash (a timing permission read from hand), the
 * *cast* trigger (it resolves before the Aura itself, and targets creatures you don't control),
 * and the ordinary +1/+2 on the host.
 */
class SupernaturalRescueScenarioTest : ScenarioTestBase() {

    private fun canCastFromHand(game: TestGame, playerNumber: Int, cardName: String): Boolean {
        val cardIds = game.findCardsInHand(playerNumber, cardName).toSet()
        return game.getLegalActions(playerNumber).any { (it.action as? CastSpell)?.cardId in cardIds }
    }

    init {
        context("Supernatural Rescue") {

            test("conditional flash: castable on the opponent's turn only while you control a Spirit") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Supernatural Rescue")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("no Spirit — sorcery timing, and it is not your turn") {
                    canCastFromHand(game, 1, "Supernatural Rescue") shouldBe false
                }
            }

            test("controlling a Spirit grants flash on the opponent's turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Supernatural Rescue")
                    .withCardOnBattlefield(1, "Dreamshackle Geist", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Dreamshackle Geist is a Spirit, so the Aura has flash") {
                    canCastFromHand(game, 1, "Supernatural Rescue") shouldBe true
                }
            }

            test("the cast trigger taps up to two creatures you don't control, and the Aura buffs its host") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Supernatural Rescue")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Wall of Wood", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!
                val wall = game.findPermanent("Wall of Wood")!!

                game.castSpell(1, "Supernatural Rescue", targetId = bears).error shouldBe null

                withClue("the cast trigger asks for its own targets while the Aura is still on the stack") {
                    game.selectTargets(listOf(giant, wall)).error shouldBe null
                }
                game.resolveStack()

                withClue("both opposing creatures are tapped") {
                    game.state.getEntity(giant)?.has<TappedComponent>() shouldBe true
                    game.state.getEntity(wall)?.has<TappedComponent>() shouldBe true
                }
                withClue("your own creature was never a legal target for the trigger") {
                    game.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
                }
                withClue("the Aura resolved onto your creature: 2/2 + 1/2 = 3/4") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 4
                }
            }
        }
    }
}
