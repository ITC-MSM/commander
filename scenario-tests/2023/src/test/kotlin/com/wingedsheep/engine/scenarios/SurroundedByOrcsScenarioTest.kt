package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Surrounded by Orcs (LTR #73) — {3}{U} Sorcery.
 *
 * "Amass Orcs 3, then target player mills X cards, where X is the amassed Army's power."
 *
 * "then" (vs. Foray of Orcs' "When you do") means amass and mill resolve as a single
 * sequenced effect on a cast-time target. The mill amount reads the freshly-amassed
 * Army's projected power, exercising `EntityReference.AmassedArmy` through a
 * `CompositeEffect` of `[Amass, Mill]`.
 */
class SurroundedByOrcsScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Surrounded by Orcs") {

            test("amasses Orcs 3 (0/0 token → 3/3) and mills target player for 3") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Surrounded by Orcs")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Stack opponent's library with 5 distinct cards so we can count how many
                // moved to the graveyard.
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val cast = game.castSpellTargetingPlayer(1, "Surrounded by Orcs", 2)
                withClue("Casting Surrounded by Orcs should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // Amass Orcs 3 created a 0/0 Orc Army token and put three +1/+1 counters
                // on it — it's a 3/3 by projection.
                val army = game.findPermanent("Orc Army")
                    ?: error("Surrounded by Orcs should have created an Orc Army")
                val counters = game.state.getEntity(army)?.get<CountersComponent>()
                withClue("Amass Orcs 3 places three +1/+1 counters") {
                    counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
                }
                withClue("Projected power of a 0/0 + three +1/+1 counters is 3") {
                    projector.project(game.state).getPower(army) shouldBe 3
                }

                // X = amassed Army's power = 3 → opponent mills three cards into graveyard.
                withClue("Opponent should have milled three cards") {
                    game.graveyardSize(2) shouldBe 3
                }
                withClue("Opponent's library should have two cards remaining (5 - 3 milled)") {
                    game.librarySize(2) shouldBe 2
                }
            }
        }
    }
}
