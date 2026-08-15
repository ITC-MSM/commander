package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dragon's Prey (TDM #79) and the SDK primitive it introduces:
 * [com.wingedsheep.sdk.scripting.CostModification.IncreaseGenericIfAnyTargetMatches].
 *
 * Dragon's Prey — {2}{B} Instant.
 *   "This spell costs {2} more to cast if it targets a Dragon. Destroy target creature."
 *
 * The cost-increase variant mirrors Dire Downdraft's `FixedIfAnyTargetMatches` reduction, but
 * in the increase direction: the tax applies at cast resolution exactly when a chosen target
 * matches the Dragon filter, and is treated as not-applying for affordability enumeration (the
 * minimum possible cost targets a non-Dragon).
 */
class DragonsPreyScenarioTest : ScenarioTestBase() {

    private val calculator = CostCalculator(cardRegistry)

    init {
        context("cost increase if it targets a Dragon") {

            test("base cost {2}{B} = 3 mana with no targets chosen (enumeration minimum)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .build()
                val me = game.player1Id
                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Dragon's Prey"), me,
                )
                cost.cmc shouldBe 3
                cost.genericAmount shouldBe 2
            }

            test("targeting a non-Dragon creature does not raise the cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()
                val me = game.player1Id
                val giant = game.findPermanent("Hill Giant")!!

                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Dragon's Prey"), me, listOf(giant),
                )
                withClue("Hill Giant is not a Dragon, so the {2} tax should not apply") {
                    cost.cmc shouldBe 3
                }
            }

            test("targeting a Dragon raises the cost by {2} → 5 mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .withCardOnBattlefield(2, "Boulderborn Dragon")
                    .build()
                val me = game.player1Id
                val dragon = game.findPermanent("Boulderborn Dragon")!!

                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Dragon's Prey"), me, listOf(dragon),
                )
                withClue("Boulderborn Dragon is a Dragon, so the {2} tax applies: {4}{B}") {
                    cost.cmc shouldBe 5
                    cost.genericAmount shouldBe 4
                }
            }
        }

        context("destroy target creature") {

            test("destroys a non-Dragon when enough mana is available") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.castSpell(1, "Dragon's Prey", giant).error shouldBe null
                game.resolveStack()

                game.findPermanent("Hill Giant") shouldBe null
                game.findCardsInGraveyard(2, "Hill Giant").size shouldBe 1
            }

            test("destroys a Dragon when the {2} tax is paid (5 lands available)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardOnBattlefield(2, "Boulderborn Dragon")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Boulderborn Dragon")!!
                game.castSpell(1, "Dragon's Prey", dragon).error shouldBe null
                game.resolveStack()

                game.findPermanent("Boulderborn Dragon") shouldBe null
                game.findCardsInGraveyard(2, "Boulderborn Dragon").size shouldBe 1
            }

            test("casting against a Dragon with only 3 mana is rejected (tax unaffordable)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragon's Prey")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Boulderborn Dragon")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Boulderborn Dragon")!!
                val playerId = game.player1Id
                val cardId = game.state.getHand(playerId).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Dragon's Prey"
                }
                val result = game.execute(
                    CastSpell(playerId, cardId, listOf(ChosenTarget.Permanent(dragon))),
                )
                withClue("5 mana required for a Dragon target, only 3 available") {
                    (result.error != null) shouldBe true
                }
                game.findPermanent("Boulderborn Dragon") shouldBe dragon
            }
        }
    }
}
