package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Prishe's Wanderings (FIN #193).
 *
 * Prishe's Wanderings — {2}{G} Instant.
 *   "Search your library for a basic land card or Town card, put it onto the battlefield
 *    tapped, then shuffle. When you search your library this way, put a +1/+1 counter on
 *    target creature you control."
 */
class PrishesWanderingsScenarioTest : ScenarioTestBase() {

    init {
        test("fetches a basic land and puts a +1/+1 counter on the targeted creature") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Prishe's Wanderings")
                .withCardOnBattlefield(1, "Rufus Shinra") // a creature you control (2/4)
                .withLandsOnBattlefield(1, "Forest", 3)
                .withCardInLibrary(1, "Plains")
                .build()

            val rufus = game.findPermanent("Rufus Shinra")!!
            game.castSpell(1, "Prishe's Wanderings", targetId = rufus).error shouldBe null
            game.resolveStack() // pauses at the land search

            if (game.hasPendingDecision()) {
                game.selectCards(game.findCardsInLibrary(1, "Plains")).error shouldBe null
                game.resolveStack()
            }

            withClue("the fetched land entered the battlefield") {
                game.isOnBattlefield("Plains") shouldBe true
            }
            withClue("the targeted creature has a +1/+1 counter") {
                val counters = game.state.getEntity(rufus)
                    ?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                counters shouldBe 1
            }
        }
    }
}
