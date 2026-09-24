package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cruel Calculations (FRA #26) — draw X, where X is the number of cards put into target player's
 * graveyard from their library this turn.
 */
class CruelCalculationsScenarioTest : ScenarioTestBase() {

    init {
        fun game(): TestGame {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Tome Scour")
                .withCardInHand(1, "Cruel Calculations")
                .withCardInHand(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            repeat(10) { b = b.withCardInLibrary(1, "Island") }
            repeat(10) { b = b.withCardInLibrary(2, "Island") }
            return b.build()
        }

        context("Cruel Calculations") {
            test("draws one card for each card milled from the target player's library this turn") {
                val game = game()
                game.castSpellTargetingPlayer(1, "Tome Scour", 2).error shouldBe null
                game.resolveStack()
                game.graveyardSize(2) shouldBe 5

                game.castSpellTargetingPlayer(1, "Cruel Calculations", 2).error shouldBe null
                game.resolveStack()

                withClue("five cards were milled, so the caster draws five") {
                    game.handSize(1) shouldBe 5
                    game.librarySize(1) shouldBe 5
                }
            }

            test("counts only the targeted player's library-to-graveyard moves") {
                val game = game()
                game.castSpellTargetingPlayer(1, "Tome Scour", 2).error shouldBe null
                game.resolveStack()

                game.castSpellTargetingPlayer(1, "Cruel Calculations", 1).error shouldBe null
                game.resolveStack()

                withClue("nothing went from your own library to your graveyard") {
                    game.handSize(1) shouldBe 0
                    game.librarySize(1) shouldBe 10
                }
            }
        }
    }
}
