package com.wingedsheep.engine.scenarios

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
 * Scenario tests for Lightfoot Technique (TDM #14).
 *
 * {1}{W} Instant.
 *   "Put a +1/+1 counter on target creature. It gains flying and indestructible until end of turn."
 */
class LightfootTechniqueScenarioTest : ScenarioTestBase() {

    init {
        context("Lightfoot Technique") {

            test("adds a +1/+1 counter and grants flying + indestructible until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lightfoot Technique")
                    .withCardOnBattlefield(1, "Centaur Courser") // 3/3, no flying
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val creature = game.findPermanent("Centaur Courser")!!

                game.castSpell(1, "Lightfoot Technique", targetId = creature).error shouldBe null
                game.resolveStack()

                withClue("Centaur Courser gets a +1/+1 counter") {
                    val counters = game.state.getEntity(creature)
                        ?.get<CountersComponent>()
                        ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 1
                }

                val projected = StateProjector().project(game.state)
                withClue("It is now a 4/4 (3/3 + counter)") {
                    projected.getPower(creature) shouldBe 4
                    projected.getToughness(creature) shouldBe 4
                }
                withClue("It gains flying and indestructible") {
                    projected.hasKeyword(creature, Keyword.FLYING) shouldBe true
                    projected.hasKeyword(creature, Keyword.INDESTRUCTIBLE) shouldBe true
                }
            }
        }
    }
}
