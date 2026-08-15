package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Run Behind {3}{U} Instant — Secrets of Strixhaven #66.
 *
 * "This spell costs {1} less to cast if it targets an attacking creature.
 *  Target creature's owner puts it on their choice of the top or bottom of their library."
 *
 * The cost-reduction-when-targeting sibling of Dire Downdraft. Tests the
 * `FixedIfAnyTargetMatches(IsAttacking)` reduction and the top/bottom library bounce.
 */
class RunBehindScenarioTest : ScenarioTestBase() {

    private val calculator = CostCalculator(cardRegistry)

    init {
        context("Run Behind") {

            test("base cost is {3}{U} = 4 mana with no target chosen") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Run Behind")
                    .build()
                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Run Behind"), game.player1Id,
                )
                cost.cmc shouldBe 4
            }

            test("targeting a non-attacking creature does not reduce the cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Run Behind")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .build()
                val bears = game.findPermanent("Grizzly Bears")!!
                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Run Behind"), game.player1Id, listOf(bears),
                )
                withClue("Grizzly Bears is not attacking, so the {1} discount should not apply") {
                    cost.cmc shouldBe 4
                }
            }

            test("targeting an attacking creature reduces the cost by {1} → 3 mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(2, "Run Behind")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                // Player 1 attacks with Grizzly Bears.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null

                val bears = game.findPermanent("Grizzly Bears")!!
                val cost = calculator.calculateEffectiveCost(
                    game.state, cardRegistry.requireCard("Run Behind"), game.player2Id, listOf(bears),
                )
                withClue("Grizzly Bears is attacking, so the {1} discount applies: {2}{U}") {
                    cost.cmc shouldBe 3
                }
            }

            test("resolution: owner puts the creature on top of their library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Run Behind")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Run Behind", bears).error shouldBe null
                game.resolveStack()

                // The owner (player 2) chooses top or bottom of their library.
                withClue("owner chooses top/bottom") {
                    (game.getPendingDecision() is ChooseOptionDecision) shouldBe true
                }
                val decision = game.getPendingDecision() as ChooseOptionDecision
                game.submitDecision(OptionChosenResponse(decision.id, 0)) // first option (top)
                game.resolveStack()

                withClue("Grizzly Bears left the battlefield") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
                withClue("Grizzly Bears is back in player 2's library") {
                    game.findCardsInLibrary(2, "Grizzly Bears").isNotEmpty() shouldBe true
                }
            }
        }
    }
}
