package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Preacher of the Schism (LCI #113) — {2}{B} Creature — Vampire Cleric, 2/4, Rare.
 *
 * "Deathtouch
 *  Whenever this creature attacks the player with the most life or tied for most life, create a 1/1
 *  white Vampire creature token with lifelink.
 *  Whenever this creature attacks while you have the most life or are tied for most life, you draw a
 *  card and you lose 1 life."
 *
 * Pins the new `PlayerHasMostLife` condition on both axes: the attacked player (`DefendingPlayer`,
 * resolved from the attacker's AttackingComponent) for the token trigger, and the controller (`You`)
 * for the draw trigger — including the tied case where both fire.
 */
class PreacherOfTheSchismScenarioTest : ScenarioTestBase() {

    init {
        context("Preacher of the Schism") {

            test("attacking the higher-life opponent makes a Vampire, but you don't draw") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Preacher of the Schism")
                    .withCardInLibrary(1, "Swamp")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 25)   // the defender has the most life
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Preacher of the Schism" to 2)).error shouldBe null
                game.resolveStack()

                withClue("attacked player has the most life → one Vampire token") {
                    game.findPermanents("Vampire Token").size shouldBe 1
                }
                withClue("you do NOT have the most life → no draw trigger") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("attacking while you have the most life draws and loses 1, no Vampire") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Preacher of the Schism")
                    .withCardInLibrary(1, "Swamp")
                    .withLifeTotal(1, 25)   // you have the most life
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Preacher of the Schism" to 2)).error shouldBe null
                game.resolveStack()

                withClue("you have the most life → draw a card and lose 1 life") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore + 1
                    game.getLifeTotal(1) shouldBe 24
                }
                withClue("attacked player does NOT have the most life → no Vampire token") {
                    game.findPermanents("Vampire Token").size shouldBe 0
                }
            }

            test("tied for most life fires both triggers") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Preacher of the Schism")
                    .withCardInLibrary(1, "Swamp")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)   // tied — both "most or tied"
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.state.getHand(game.player1Id).size

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Preacher of the Schism" to 2)).error shouldBe null
                game.resolveStack()

                withClue("tied: the attacked player counts as most → Vampire token") {
                    game.findPermanents("Vampire Token").size shouldBe 1
                }
                withClue("tied: you count as most → draw and lose 1") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore + 1
                    game.getLifeTotal(1) shouldBe 19
                }
            }
        }
    }
}
