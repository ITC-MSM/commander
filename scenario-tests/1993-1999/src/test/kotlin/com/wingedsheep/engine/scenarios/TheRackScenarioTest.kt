package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.engine.state.components.battlefield.chosenOpponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Rack (ATQ #72).
 *
 * {1} Artifact — "As this artifact enters, choose an opponent. At the beginning of the chosen
 * player's upkeep, this artifact deals X damage to that player, where X is 3 minus the number of
 * cards in their hand."
 *
 * Exercises the chosen-player step trigger: `StepEvent(UPKEEP, Player.ChosenOpponent)` now resolves
 * in `TriggerMatcher.matchesPlayerForStep` against the opponent stored on the source when it
 * entered, so the trigger fires only on the chosen player's upkeep — not on every player's.
 */
class TheRackScenarioTest : ScenarioTestBase() {

    /**
     * Player 1 controls The Rack and has chosen [chosenPlayerNumber]. The game begins on
     * [activePlayerNumber]'s UNTAP and advances into their UPKEEP, resolving any trigger.
     * [chosenHandCount] generic cards are put into the chosen player's hand.
     */
    private fun runUpkeep(
        activePlayerNumber: Int,
        chosenPlayerNumber: Int,
        chosenHandCount: Int,
    ): TestGame {
        val builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "The Rack")
            .withLifeTotal(chosenPlayerNumber, 20)
            .withActivePlayer(activePlayerNumber)
            .inPhase(Phase.BEGINNING, Step.UNTAP)
        repeat(chosenHandCount) { builder.withCardInHand(chosenPlayerNumber, "Mountain") }
        val game = builder.build()

        // Record the chosen opponent on The Rack (as the EntersWithChoice replacement would).
        val rack = game.findPermanent("The Rack")!!
        val chosenId = if (chosenPlayerNumber == 1) game.player1Id else game.player2Id
        game.state = game.state.updateEntity(rack) { c ->
            c.with(CastChoicesComponent(chosen = mapOf(
                ChoiceSlot.OPPONENT to ChoiceValue.EntityChoice(chosenId)
            )))
        }

        game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
        game.resolveStack()
        return game
    }

    init {
        context("The Rack") {

            test("chosen player with empty hand takes 3 on their upkeep (3 - 0)") {
                val game = runUpkeep(activePlayerNumber = 2, chosenPlayerNumber = 2, chosenHandCount = 0)
                withClue("Empty hand → 3 - 0 = 3 damage → 20 - 3 = 17") {
                    game.getLifeTotal(2) shouldBe 17
                }
            }

            test("chosen player with 1 card takes 2 (3 - 1)") {
                val game = runUpkeep(activePlayerNumber = 2, chosenPlayerNumber = 2, chosenHandCount = 1)
                withClue("Hand of 1 → 3 - 1 = 2 damage → 20 - 2 = 18") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("chosen player with 3+ cards takes no damage (negative deals 0)") {
                val game = runUpkeep(activePlayerNumber = 2, chosenPlayerNumber = 2, chosenHandCount = 5)
                withClue("Hand of 5 → 3 - 5 = -2 → no damage → life unchanged at 20") {
                    game.getLifeTotal(2) shouldBe 20
                }
            }

            test("the trigger does NOT fire on a non-chosen player's upkeep") {
                // The Rack chose player 2, but the game runs through player 1's (controller's) upkeep.
                val game = runUpkeep(activePlayerNumber = 1, chosenPlayerNumber = 2, chosenHandCount = 0)
                withClue("Player 1's upkeep is not the chosen player's — no damage to player 2") {
                    game.getLifeTotal(2) shouldBe 20
                }
            }

            // End-to-end: cast The Rack from hand and resolve the real EntersWithChoice(OPPONENT)
            // replacement, proving the chosen player it records under ChoiceSlot.OPPONENT is the same
            // slot the ChosenOpponentUpkeep trigger reads — not just the injected-component shortcut.
            test("casting The Rack records the chosen opponent and damages them on their upkeep") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "The Rack")
                    .withLandsOnBattlefield(1, "Plains", 1) // pays {1}
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "The Rack").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                // Resolving the spell raises the "choose an opponent" prompt; pick the sole opponent.
                game.resolveStack()
                val choose = game.getPendingDecision()
                withClue("The Rack's EntersWithChoice surfaces an opponent prompt as it resolves") {
                    (choose is ChooseOptionDecision) shouldBe true
                }
                game.submitDecision(OptionChosenResponse(choose!!.id, 0))
                game.resolveStack()

                val rack = game.findPermanent("The Rack")!!
                withClue("the chosen opponent is recorded under ChoiceSlot.OPPONENT") {
                    game.state.getEntity(rack)!!.chosenOpponent() shouldBe game.player2Id
                }

                // Advance into the chosen opponent's (player 2's) upkeep — empty hand → 3 damage.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                withClue("chosen opponent has an empty hand at their upkeep → 3 - 0 = 3 → 20 - 3 = 17") {
                    game.getLifeTotal(2) shouldBe 17
                }
            }
        }
    }
}
