package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Wildwood Scourge (M21 #214, reprinted as FDN #236) — {X}{G} 0/0 Creature — Hydra.
 *
 * "This creature enters with X +1/+1 counters on it.
 *  Whenever one or more +1/+1 counters are put on another non-Hydra creature you control,
 *  put a +1/+1 counter on this creature."
 *
 * Pins the X-scaled entry and the counter-watcher's filter: it grows off a non-Hydra
 * creature you control, but not off another Hydra.
 */
class WildwoodScourgeScenarioTest : ScenarioTestBase() {

    private fun plusOnes(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Wildwood Scourge") {

            test("enters with X +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withCardInHand(1, "Wildwood Scourge")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Wildwood Scourge", xValue = 4).error shouldBe null
                game.resolveStack()

                val scourge = game.findPermanent("Wildwood Scourge")!!
                withClue("X = 4 means four +1/+1 counters") {
                    plusOnes(game, scourge) shouldBe 4
                }
            }

            test("grows when +1/+1 counters land on another non-Hydra creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withCardInHand(1, "Wildwood Scourge")
                    .withCardInHand(1, "New Horizons")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // The Scourge is a printed 0/0, so it has to be cast for X — dropping a
                // counterless copy straight onto the battlefield just feeds it to SBAs.
                game.castXSpell(1, "Wildwood Scourge", xValue = 1).error shouldBe null
                game.resolveStack()

                val scourge = game.findPermanent("Wildwood Scourge")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                plusOnes(game, scourge) shouldBe 1

                // New Horizons' enters trigger puts a +1/+1 counter on the Bears.
                game.castSpell(1, "New Horizons", game.findPermanent("Forest")!!).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                plusOnes(game, bears) shouldBe 1
                withClue("the Bears are a non-Hydra creature you control, so the Scourge grows") {
                    plusOnes(game, scourge) shouldBe 2
                }
            }

            test("does not grow off counters on another Hydra you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Forest", 7)
                    .withCardInHand(1, "Wildwood Scourge")
                    .withCardInHand(1, "Wildwood Scourge")
                    .withCardInHand(1, "New Horizons")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                repeat(2) {
                    game.castXSpell(1, "Wildwood Scourge", xValue = 1).error shouldBe null
                    game.resolveStack()
                }

                val hydras = game.findAllPermanents("Wildwood Scourge")
                hydras.size shouldBe 2

                game.castSpell(1, "New Horizons", game.findPermanent("Forest")!!).error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(hydras.first())).error shouldBe null
                game.resolveStack()

                plusOnes(game, hydras.first()) shouldBe 2
                withClue("the other Hydra is excluded by the filter, so it doesn't grow") {
                    plusOnes(game, hydras.last()) shouldBe 1
                }
            }
        }
    }
}
