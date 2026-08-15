package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Pensive Professor (Secrets of Strixhaven #63).
 *
 * Pensive Professor ({1}{U}{U}, 0/2, Human Wizard):
 *   Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this
 *     creature's power or toughness, put a +1/+1 counter on this creature.)
 *   Whenever one or more +1/+1 counters are put on this creature, draw a card.
 *
 * Exercises the composition of the existing Increment ability word with a counters-placed (bound to
 * SELF) draw payoff. Casting a spell that beats the smaller stat (0 power) grows the creature, and
 * each such growth draws a card.
 */
class PensiveProfessorScenarioTest : ScenarioTestBase() {

    private fun TestGame.plusCounters(name: String): Int {
        val id = findPermanent(name) ?: return -1
        return state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Pensive Professor — Increment growth draws a card") {

            test("a spell beating the 0 power grows it and draws a card; equal mana does neither") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Pensive Professor", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears") // 2-mana spell
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val handBefore = game.state.getHand(game.player1Id).size

                // 2-mana spell: 2 > min(0, 2) = 0 -> +1/+1 counter, which draws a card.
                game.castSpell(1, "Grizzly Bears")
                game.resolveStack()

                withClue("a 2-mana spell grows the 0/2 (2 > 0)") {
                    game.plusCounters("Pensive Professor") shouldBe 1
                }
                // After casting Grizzly Bears (left hand) and drawing 1 from the counter trigger,
                // the net hand size change is: -1 (cast) + 1 (draw) = 0.
                withClue("the counters-placed trigger draws a card (net hand size unchanged after the cast)") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore
                }
            }
        }
    }
}
