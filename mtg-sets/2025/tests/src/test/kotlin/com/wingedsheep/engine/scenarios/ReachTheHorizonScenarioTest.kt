package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Reach the Horizon (FIN #195).
 *
 * Reach the Horizon — {3}{G} Sorcery.
 *   "Search your library for up to two basic land cards and/or Town cards with different
 *    names, put them onto the battlefield tapped, then shuffle."
 */
class ReachTheHorizonScenarioTest : ScenarioTestBase() {

    init {
        test("fetches two different-named basic/Town lands onto the battlefield tapped") {
            val game = scenario()
                .withPlayers()
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardInHand(1, "Reach the Horizon")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Reach the Horizon").error shouldBe null
            game.resolveStack() // pauses at the search selection

            val plains = game.findCardsInLibrary(1, "Plains")
            val island = game.findCardsInLibrary(1, "Island")
            withClue("the search decision is offered") { game.hasPendingDecision() shouldBe true }
            game.selectCards(plains + island).error shouldBe null
            game.resolveStack()

            withClue("both fetched lands are on the battlefield") {
                game.isOnBattlefield("Plains") shouldBe true
                game.isOnBattlefield("Island") shouldBe true
            }
            withClue("the fetched Plains entered tapped") {
                val plainsId = game.findPermanent("Plains")!!
                game.state.getEntity(plainsId)?.get<TappedComponent>() shouldNotBe null
            }
        }
    }
}
