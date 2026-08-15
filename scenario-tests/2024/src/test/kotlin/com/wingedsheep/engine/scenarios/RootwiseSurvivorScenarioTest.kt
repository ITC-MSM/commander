package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rootwise Survivor (DSK #196).
 *
 * Rootwise Survivor — {3}{G}{G} Creature — Human Survivor, 3/4
 *   "Haste
 *    Survival — At the beginning of your second main phase, if this creature is tapped, put
 *    three +1/+1 counters on up to one target land you control. That land becomes a 0/0
 *    Elemental creature in addition to its other types. It gains haste until your next turn."
 *
 * Verifies the intervening-if (only when the Survivor is tapped), the three +1/+1 counters,
 * the permanent animate-into-Elemental (still a land), the granted haste, and that an
 * untapped Survivor produces no trigger.
 */
class RootwiseSurvivorScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Rootwise Survivor — Survival trigger") {

            test("a tapped Survivor animates a target land into a 3/3 Elemental land with haste") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rootwise Survivor", tapped = true)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!

                // Advance to the second main phase — the Survival trigger fires (source is tapped).
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                var guard = 0
                while (game.state.pendingDecision !is ChooseTargetsDecision && guard < 20) {
                    game.resolveStack()
                    guard++
                }
                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the Survival trigger; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(forest))))
                game.resolveStack()

                withClue("The land has three +1/+1 counters") {
                    val counters = game.state.getEntity(forest)?.get<CountersComponent>()?.counters ?: emptyMap()
                    counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 3
                }

                val projected = projector.project(game.state)
                withClue("The land is now an Elemental creature, still a land") {
                    projected.hasType(forest, "LAND") shouldBe true
                    projected.hasType(forest, "CREATURE") shouldBe true
                }
                withClue("Base 0/0 plus three +1/+1 counters = 3/3") {
                    projected.getPower(forest) shouldBe 3
                    projected.getToughness(forest) shouldBe 3
                }
                withClue("The land gains haste") {
                    projected.hasKeyword(forest, Keyword.HASTE) shouldBe true
                }
            }

            test("an untapped Survivor does NOT fire the Survival trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Rootwise Survivor", tapped = false)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                repeat(5) { if (game.hasPendingDecision()) Unit else game.resolveStack() }

                withClue("No Survival trigger — the Survivor is untapped, so the intervening-if fails") {
                    (game.state.pendingDecision is ChooseTargetsDecision) shouldBe false
                    val counters = game.state.getEntity(forest)?.get<CountersComponent>()?.counters ?: emptyMap()
                    counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe null
                    projector.project(game.state).hasType(forest, "CREATURE") shouldBe false
                }
            }
        }
    }
}
