package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Scytheclaw Raptor (LCI #165) — {2}{R} Creature — Dinosaur, 4/3, Uncommon.
 *
 * "Whenever a player casts a spell, if it's not their turn, this creature deals 4 damage to them."
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.conditions.IsPlayersTurn] condition resolved
 * against `Player.TriggeringPlayer`: the intervening-if "if it's not their turn" is relative to the
 * *casting* player, not Scytheclaw's controller. So an opponent casting during your turn takes 4,
 * but the same opponent casting on their own turn takes none.
 */
class ScytheclawRaptorScenarioTest : ScenarioTestBase() {

    init {
        context("Scytheclaw Raptor") {

            test("an opponent casting a spell during your turn takes 4 damage") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Scytheclaw Raptor")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val raptor = game.findPermanent("Scytheclaw Raptor")!!

                // Hand priority to the opponent so they can cast during your turn.
                game.passPriority()
                val cast = game.castSpell(2, "Shock", raptor)
                withClue("Shock should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Scytheclaw Raptor deals 4 to the opponent — it isn't their turn") {
                    game.getLifeTotal(2) shouldBe 16
                }
            }

            test("an opponent casting a spell on their own turn takes no damage") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Scytheclaw Raptor")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)   // it's the opponent's turn now
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val raptor = game.findPermanent("Scytheclaw Raptor")!!

                val cast = game.castSpell(2, "Shock", raptor)
                withClue("Shock should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("no damage — the caster is on their own turn, so the intervening-if is false") {
                    game.getLifeTotal(2) shouldBe 20
                }
            }
        }
    }
}
