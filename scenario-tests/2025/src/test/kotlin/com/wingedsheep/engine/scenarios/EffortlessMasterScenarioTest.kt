package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Effortless Master (TDM #181) — {2}{U}{R} Orc Monk, 4/3, Vigilance, Menace.
 *
 * "This creature enters with two +1/+1 counters on it if you've cast two or more spells this turn."
 *
 * The spell-count is recorded at cast time, so casting Effortless Master itself counts. Casting one
 * prior spell (Opt) and then Effortless Master = two spells this turn → it enters with two counters.
 * Casting Effortless Master as the only spell of the turn = one spell → no counters.
 */
class EffortlessMasterScenarioTest : ScenarioTestBase() {

    init {
        context("Effortless Master's conditional enters-with-counters") {

            test("enters with two +1/+1 counters after casting two or more spells this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hill Giant")
                    .withCardInHand(1, "Effortless Master")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // First spell of the turn: Hill Giant ({3}{R}).
                val first = game.castSpell(1, "Hill Giant")
                withClue("Casting Hill Giant should succeed: ${first.error}") { first.error shouldBe null }
                game.resolveStack()

                // Second spell: Effortless Master itself. At ETB, two spells have been cast.
                val cast = game.castSpell(1, "Effortless Master")
                withClue("Casting Effortless Master should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val master = game.findPermanent("Effortless Master")!!
                val counters = game.state.getEntity(master)?.get<CountersComponent>()
                withClue("Effortless Master enters with two +1/+1 counters") {
                    (counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 2
                }
            }

            test("enters with no counters when it is the only spell cast this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Effortless Master")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Effortless Master")
                withClue("Casting Effortless Master should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val master = game.findPermanent("Effortless Master")!!
                val counters = game.state.getEntity(master)?.get<CountersComponent>()
                withClue("Effortless Master enters with no +1/+1 counters") {
                    (counters?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 0
                }
            }
        }
    }
}
