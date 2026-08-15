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
 * Scenario test for Take Up the Shield (DMU #35, reprinted in OTJ #34) — {1}{W} Instant.
 *
 * "Put a +1/+1 counter on target creature. It gains lifelink and indestructible until end of
 *  turn."
 *
 * Canonical definition lives in Dominaria United; the OTJ printing contributes only a Printing row.
 */
class TakeUpTheShieldScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Take Up the Shield") {

            test("adds a +1/+1 counter and grants lifelink and indestructible") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Take Up the Shield")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                val powerBefore = projector.getProjectedPower(game.state, bear)

                game.castSpell(1, "Take Up the Shield", bear).error shouldBe null
                game.resolveStack()

                withClue("the targeted creature has a +1/+1 counter") {
                    val counters = game.state.getEntity(bear)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 1
                }
                withClue("the +1/+1 counter raises projected power by 1") {
                    projector.getProjectedPower(game.state, bear) shouldBe powerBefore + 1
                }
                withClue("the creature gains lifelink until end of turn") {
                    projector.hasProjectedKeyword(game.state, bear, Keyword.LIFELINK) shouldBe true
                }
                withClue("the creature gains indestructible until end of turn") {
                    projector.hasProjectedKeyword(game.state, bear, Keyword.INDESTRUCTIBLE) shouldBe true
                }
            }
        }
    }
}
