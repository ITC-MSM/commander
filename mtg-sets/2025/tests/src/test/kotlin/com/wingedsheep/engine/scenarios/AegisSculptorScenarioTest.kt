package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Aegis Sculptor (TDM #35).
 *
 * "{3}{U} Creature — Bird Wizard 2/3. Flying, Ward {2}.
 *  At the beginning of your upkeep, you may exile two cards from your graveyard. If you do,
 *  put a +1/+1 counter on this creature."
 *
 * The test advances from player 2's turn into player 1's upkeep so the "your upkeep" trigger
 * fires for the Sculptor's controller. Verifies: with two graveyard cards, exiling both adds a
 * +1/+1 counter; with fewer than two, declining adds no counter.
 */
class AegisSculptorScenarioTest : ScenarioTestBase() {

    init {
        context("Aegis Sculptor upkeep") {

            test("exiling two cards from graveyard puts a +1/+1 counter on it") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aegis Sculptor", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Library fuel so neither player decks during the step advances.
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                // Advance to player 1's upkeep so the "your upkeep" trigger fires.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                // MayEffect: accept, then choose exactly two graveyard cards to exile.
                if (game.hasPendingDecision()) {
                    game.answerYesNo(true)
                    game.resolveStack()
                }
                if (game.hasPendingDecision()) {
                    val gy = game.state.getGraveyard(game.player1Id)
                    game.selectCards(gy.take(2))
                    game.resolveStack()
                }

                withClue("Both graveyard cards should have been exiled") {
                    game.state.getGraveyard(game.player1Id).size shouldBe 0
                }

                val sculptorId = game.findPermanent("Aegis Sculptor")!!
                val counters = game.state.getEntity(sculptorId)?.get<CountersComponent>()
                withClue("Aegis Sculptor should have a +1/+1 counter after exiling two cards") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }

            test("declining the upkeep ability adds no counter") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aegis Sculptor", summoningSickness = false)
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    game.answerYesNo(false)
                    game.resolveStack()
                }

                withClue("Declining should leave the graveyard intact") {
                    game.state.getGraveyard(game.player1Id).size shouldBe 2
                }

                val sculptorId = game.findPermanent("Aegis Sculptor")!!
                val counters = game.state.getEntity(sculptorId)?.get<CountersComponent>()
                withClue("Aegis Sculptor should have no counter when declining") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 0
                }
            }
        }
    }
}
