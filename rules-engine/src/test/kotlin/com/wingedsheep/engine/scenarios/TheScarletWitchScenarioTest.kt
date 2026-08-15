package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Scarlet Witch (MSH #151) — {2}{R} 2/3 Legendary Creature.
 *
 * Oracle: "Instant and sorcery spells you cast with mana value 4 or greater cost {X} less to cast,
 * where X is The Scarlet Witch's power."
 *
 * Exercises `CostReductionSource.Dynamic(DynamicAmounts.sourcePower())` through the printed card.
 * The interesting cases are the two gates on the filter (card type and mana value), the "you cast"
 * scoping, and the fact that the amount is *her* power — a bigger creature you control must not
 * change the discount, which is exactly what the tempting
 * `GreatestPropertyAmongPermanentsYouControl(Power, named(...))` approximation would get wrong.
 *
 * Spell costs are read straight off [CostCalculator] rather than by casting, because the cost is the
 * whole assertion — see `TheLordOfTheEaglesScenarioTest` for the same pattern.
 *
 * Not covered, because the engine can't express it yet: an {X} spell whose announced X pushes its
 * mana value to 4 or more (CR 202.3e, CR 601.2b) should qualify, but `CostCalculator` evaluates
 * `ManaValueAtLeast` against the printed cost and `calculateEffectiveCost` takes no declared X, so
 * there is no input to vary. See the card's KDoc for the full note.
 */
class TheScarletWitchScenarioTest : ScenarioTestBase() {

    private fun costOf(game: TestGame, spellName: String, playerNumber: Int = 1) =
        CostCalculator(cardRegistry).calculateEffectiveCost(
            game.state,
            cardRegistry.requireCard(spellName),
            if (playerNumber == 1) game.player1Id else game.player2Id,
        )

    init {
        context("The Scarlet Witch — big instants and sorceries cost {X} less, X = her power") {

            test("without her on the battlefield, Lava Axe costs full price") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lava Axe")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                costOf(game, "Lava Axe").genericAmount shouldBe 4
            }

            test("a mana value 5 sorcery is reduced by her printed power of 2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lava Axe")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("{4}{R} minus her 2 power leaves {2}{R}") {
                    val cost = costOf(game, "Lava Axe")
                    cost.genericAmount shouldBe 2
                    cost.colorCount[Color.RED] shouldBe 1
                }
            }

            test("a mana value 3 instant is below the threshold and is not reduced") {
                // Cancel is {1}{U}{U} — mana value 3, so the filter's manaValueAtLeast(4) rejects it.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cancel")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                costOf(game, "Cancel").genericAmount shouldBe 1
            }

            test("a creature spell of mana value 5 is not an instant or sorcery and is not reduced") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Serra Angel")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                costOf(game, "Serra Angel").genericAmount shouldBe 3
            }

            test("her power is read from projected state, so anthems deepen the discount") {
                // Two Glorious Anthems make her a 4/5, clearing all four generic from Lava Axe.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lava Axe")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withCardOnBattlefield(1, "Glorious Anthem")
                    .withCardOnBattlefield(1, "Glorious Anthem")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("a 4-power Witch clears the {4} but can never eat the {R}") {
                    val cost = costOf(game, "Lava Axe")
                    cost.genericAmount shouldBe 0
                    cost.colorCount[Color.RED] shouldBe 1
                }
            }

            test("a bigger creature you control does not change the discount") {
                // Serra Angel is a 4/4; only the Witch's own power may be read.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lava Axe")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withCardOnBattlefield(1, "Serra Angel")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("the reduction is her 2 power, not the 4 power of the biggest creature") {
                    costOf(game, "Lava Axe").genericAmount shouldBe 2
                }
            }

            test("she does not discount an opponent's spells") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(2, "Lava Axe")
                    .withCardOnBattlefield(1, "The Scarlet Witch")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("'spells you cast' scopes to her controller") {
                    costOf(game, "Lava Axe", playerNumber = 2).genericAmount shouldBe 4
                }
            }

            test("an opponent's Scarlet Witch does not discount your spells either") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lava Axe")
                    .withCardOnBattlefield(2, "The Scarlet Witch")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                costOf(game, "Lava Axe").genericAmount shouldBe 4
            }
        }
    }
}
