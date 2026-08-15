package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Vile Mutilator (DSK #122).
 *
 * Vile Mutilator — {5}{B}{B} Creature — Demon, 6/5, Flying, trample.
 *   "As an additional cost to cast this spell, sacrifice a creature or enchantment.
 *    When this creature enters, each opponent sacrifices a nontoken enchantment of their choice,
 *    then sacrifices a nontoken creature of their choice."
 *
 * Verifies:
 *  - the spell cannot be cast without paying the additional sacrifice cost, and can be cast when a
 *    creature-or-enchantment is sacrificed;
 *  - the ETB makes an opponent sacrifice a nontoken enchantment and then a nontoken creature, while
 *    leaving token permanents untouched (the `.nontoken()` filter excludes them).
 */
class VileMutilatorScenarioTest : ScenarioTestBase() {

    private fun handCardId(game: TestGame, name: String) =
        game.state.getHand(game.player1Id).first {
            game.state.getEntity(it)?.get<CardComponent>()?.name == name
        }

    init {
        context("Vile Mutilator additional cost") {

            test("cannot be cast without sacrificing a creature or enchantment") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vile Mutilator")
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    // A creature the caster controls — present but NOT supplied as the sacrifice.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = handCardId(game, "Vile Mutilator")

                // No additionalCostPayment supplied -> the mandatory sacrifice cost is unpaid.
                val result = game.execute(CastSpell(playerId = game.player1Id, cardId = cardId))

                withClue("Casting without paying the additional sacrifice cost must be rejected") {
                    result.error shouldNotBe null
                }
                withClue("Vile Mutilator stays in hand when the cast is rejected") {
                    game.isOnBattlefield("Vile Mutilator") shouldBe false
                }
            }

            test("can be cast by sacrificing a creature or enchantment as the additional cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vile Mutilator")
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = handCardId(game, "Vile Mutilator")
                val sacrifice = game.findPermanent("Grizzly Bears")!!

                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(sacrifice)),
                    )
                )
                withClue("Casting while sacrificing a creature should succeed: ${result.error}") {
                    result.error shouldBe null
                }

                withClue("The sacrificed creature is paid as a cost and goes to the graveyard") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }

                game.resolveStack()

                withClue("Vile Mutilator resolves onto the battlefield") {
                    game.isOnBattlefield("Vile Mutilator") shouldBe true
                }
            }
        }

        context("Vile Mutilator ETB sacrifice") {

            test("each opponent sacrifices a nontoken enchantment then a nontoken creature; tokens are spared") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vile Mutilator")
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    // Caster's fodder for the additional cost.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // Opponent: exactly one nontoken enchantment + one nontoken creature so the
                    // forced "of their choice" sacrifices auto-resolve with no decision needed.
                    .withCardOnBattlefield(2, "Crusade")     // nontoken enchantment
                    .withCardOnBattlefield(2, "Hill Giant")  // nontoken creature
                    // Token permanents of each kind — must be ineligible for the .nontoken() filter.
                    .withCardOnBattlefield(2, "Bad Moon", isToken = true)      // token enchantment
                    .withCardOnBattlefield(2, "Grizzly Bears", isToken = true) // token creature
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cardId = handCardId(game, "Vile Mutilator")
                val sacrifice = game.findPermanent("Grizzly Bears")!! // the caster's (player 1) creature

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(sacrifice)),
                    )
                ).error shouldBe null

                game.resolveStack()

                withClue("Vile Mutilator entered, so its ETB sacrifice has resolved") {
                    game.isOnBattlefield("Vile Mutilator") shouldBe true
                }

                withClue("Opponent's nontoken enchantment is sacrificed") {
                    game.findPermanent("Crusade") shouldBe null
                    game.isInGraveyard(2, "Crusade") shouldBe true
                }
                withClue("Opponent's nontoken creature is sacrificed") {
                    game.findPermanent("Hill Giant") shouldBe null
                    game.isInGraveyard(2, "Hill Giant") shouldBe true
                }

                withClue("Opponent's token enchantment is NOT eligible and survives") {
                    game.findPermanent("Bad Moon") shouldNotBe null
                }
                withClue("Opponent's token creature is NOT eligible and survives") {
                    // player 1's Grizzly Bears was sacrificed to the cost; the only remaining
                    // Grizzly Bears is player 2's token, which must still be on the battlefield.
                    game.findPermanents("Grizzly Bears").size shouldBe 1
                }
            }
        }
    }
}
