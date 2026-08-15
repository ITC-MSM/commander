package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Atmospheric Greenhouse's ETB trigger.
 *
 * Oracle: "When this Spacecraft enters, put a +1/+1 counter on each creature you control."
 *
 * Regression for a bug found by the card linter (sdk-analysis §1.1): the ETB's
 * `ForEachInGroup` inner effect targeted `ContextTarget(0)` — which reads the (empty)
 * cast-time target list, not the iterated creature — so the trigger silently did nothing.
 * The iteration entity is addressed with `EffectTarget.Self`.
 */
class AtmosphericGreenhouseScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val permanent = game.findPermanent(name)!!
        return game.state.getEntity(permanent)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Atmospheric Greenhouse — ETB counters") {

            test("ETB puts a +1/+1 counter on each creature you control, not the opponent's") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Atmospheric Greenhouse")
                    .withCardOnBattlefield(1, "Glory Seeker")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .build()

                game.castSpell(1, "Atmospheric Greenhouse")
                game.resolveStack()

                withClue("each of the controller's creatures gets a +1/+1 counter") {
                    plusOneCounters(game, "Glory Seeker") shouldBe 1
                    plusOneCounters(game, "Centaur Courser") shouldBe 1
                }
                withClue("the opponent's creature is untouched") {
                    plusOneCounters(game, "Savannah Lions") shouldBe 0
                }
                withClue("the Spacecraft itself is not a creature yet (no charge counters)") {
                    plusOneCounters(game, "Atmospheric Greenhouse") shouldBe 0
                }
            }
        }
    }
}
