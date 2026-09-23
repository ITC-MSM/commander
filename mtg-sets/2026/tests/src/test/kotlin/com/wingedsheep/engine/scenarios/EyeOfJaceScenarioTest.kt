package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Eye of Jace (FRA #170) — upkeep surveil 1, then with seven or more cards in your graveyard it
 * sacrifices itself, deals 2 damage to each opponent, and you gain 2 life.
 */
class EyeOfJaceScenarioTest : ScenarioTestBase() {

    init {
        fun upkeepGame(): TestGame {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Eye of Jace")
                .withActivePlayer(2)
                .inPhase(Phase.ENDING, Step.END)
            repeat(6) { b = b.withCardInGraveyard(1, "Grizzly Bears") }
            repeat(4) { b = b.withCardInLibrary(1, "Island") }
            repeat(4) { b = b.withCardInLibrary(2, "Island") }
            val game = b.build()
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.activePlayerId shouldBe game.player1Id
            game.resolveStack()
            return game
        }

        context("Eye of Jace") {
            test("surveiling the seventh card into the graveyard sets off the Eye") {
                val game = upkeepGame()

                val surveil = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                game.selectCards(surveil.options).error shouldBe null
                game.resolveStack()

                game.graveyardSize(1) shouldBe 8 // six Bears, the surveilled card, and the Eye
                game.isOnBattlefield("Eye of Jace") shouldBe false
                game.getLifeTotal(2) shouldBe 18
                game.getLifeTotal(1) shouldBe 22
            }

            test("keeping the card on top leaves six cards — nothing else happens") {
                val game = upkeepGame()

                game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                game.skipSelection().error shouldBe null
                game.resolveStack()

                withClue("the graveyard check is made after the surveil") {
                    game.graveyardSize(1) shouldBe 6
                }
                game.isOnBattlefield("Eye of Jace") shouldBe true
                game.getLifeTotal(2) shouldBe 20
                game.getLifeTotal(1) shouldBe 20
            }
        }
    }
}
