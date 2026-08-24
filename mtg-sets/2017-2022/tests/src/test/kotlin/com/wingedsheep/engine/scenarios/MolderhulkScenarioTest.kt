package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario coverage for Molderhulk (GRN #190).
 *
 * {7}{B}{G} Creature — Fungus Zombie 6/6
 * "Undergrowth — This spell costs {1} less to cast for each creature card in your graveyard.
 *  When this creature enters, return target land card from your graveyard to the battlefield."
 *
 * Undergrowth as a cost reduction rather than a counter count: it reduces only the *generic*
 * portion, so {B}{G} is still owed no matter how full the graveyard is. The two halves read
 * different piles — the discount counts creature cards, the trigger returns a land card.
 */
class MolderhulkScenarioTest : ScenarioTestBase() {

    private val costCalculator by lazy { CostCalculator(cardRegistry) }

    init {
        context("Molderhulk") {

            fun board(creatureCardsInGraveyard: Int, landsInGraveyard: Int = 1) = run {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Molderhulk")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withLandsOnBattlefield(1, "Forest", 5)
                repeat(creatureCardsInGraveyard) {
                    builder = builder.withCardInGraveyard(1, "Centaur Courser")
                }
                repeat(landsInGraveyard) { builder = builder.withCardInGraveyard(1, "Mountain") }
                builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()
            }

            test("each creature card in your graveyard shaves {1} off the generic cost") {
                val game = board(creatureCardsInGraveyard = 4)

                withClue("{7} generic minus four creature cards") {
                    costCalculator.calculateEffectiveCost(
                        game.state,
                        cardRegistry.requireCard("Molderhulk"),
                        game.player1Id
                    ).genericAmount shouldBe 3
                }
            }

            test("the discount never eats the coloured pips") {
                val game = board(creatureCardsInGraveyard = 12)

                val cost = costCalculator.calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard("Molderhulk"),
                    game.player1Id
                )
                withClue("twelve creature cards floor the generic at zero") {
                    cost.genericAmount shouldBe 0
                }
                withClue("{B}{G} is still owed") {
                    cost.colorCount[Color.BLACK] shouldBe 1
                    cost.colorCount[Color.GREEN] shouldBe 1
                }
            }

            test("it returns a land card from your graveyard to the battlefield on entry") {
                val game = board(creatureCardsInGraveyard = 3)
                val land = game.findCardsInGraveyard(1, "Mountain").single()

                game.castSpell(1, "Molderhulk").error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(land))
                game.resolveStack()

                withClue("the Mountain leaves the graveyard for the battlefield") {
                    game.findCardsInGraveyard(1, "Mountain").size shouldBe 0
                    game.findPermanent("Mountain") shouldBe land
                }
            }
        }
    }
}
