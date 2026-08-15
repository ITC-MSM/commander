package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for April O'Neil, Hacktivist (TMT) — the proving card for
 * `DynamicAmount.SpellsCastThisTurn(countDistinctCardTypes = true)`.
 *
 * "At the beginning of your end step, draw a card for each card type among spells you've cast
 *  this turn."
 *
 * Proves the distinct-card-types aggregation:
 *  - casting one artifact creature spell (two card types from a single spell) draws 2, showing the
 *    amount counts card *types* (unioned across a spell's type line), not the number of spells,
 *  - casting nothing draws 0.
 */
class AprilONeilHacktivistScenarioTest : ScenarioTestBase() {

    init {
        context("April O'Neil, Hacktivist end step") {

            test("one artifact creature spell (Artifact + Creature) draws two cards") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "April O'Neil, Hacktivist")
                    .withCardInHand(1, "Ornithopter") // {0} Artifact Creature
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                game.castSpell(1, "Ornithopter")
                game.resolveStack()

                // Hand is now empty (Ornithopter left for the battlefield).
                game.handSize(1) shouldBe 0

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Artifact + Creature = 2 card types → draw 2") {
                    game.handSize(1) shouldBe 2
                }
            }

            test("casting no spells this turn draws nothing") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "April O'Neil, Hacktivist")
                    .withCardInHand(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("No spells cast → 0 card types → draw 0") {
                    game.handSize(1) shouldBe 1
                }
            }
        }
    }
}
