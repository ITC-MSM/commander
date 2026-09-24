package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Entrust the Spark (FRA #131) — "You may sacrifice a planeswalker. If you do, search your library
 * for a planeswalker card, put it onto the battlefield, then shuffle."
 */
class EntrustTheSparkScenarioTest : ScenarioTestBase() {
    init {
        fun build() = scenario().withPlayers()
            .withCardInHand(1, "Entrust the Spark")
            .withCardOnBattlefield(1, "Ajani Goldmane")
            .withLandsOnBattlefield(1, "Forest", 3)
            .withLandsOnBattlefield(1, "Island", 2)
            .withCardInLibrary(1, "Professor Dellian Fel")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        test("sacrificing a planeswalker fetches a planeswalker card onto the battlefield") {
            val game = build()
            game.castSpell(1, "Entrust the Spark").error shouldBe null
            game.resolveStack()

            game.answerYesNo(true).error shouldBe null
            // The only planeswalker is sacrificed automatically; the search follows.
            val dellian = game.findCardsInLibrary(1, "Professor Dellian Fel").single()
            game.selectCards(listOf(dellian)).error shouldBe null

            game.isInGraveyard(1, "Ajani Goldmane") shouldBe true
            game.isOnBattlefield("Professor Dellian Fel") shouldBe true
        }

        test("declining the sacrifice skips the search") {
            val game = build()
            game.castSpell(1, "Entrust the Spark").error shouldBe null
            game.resolveStack()

            game.answerYesNo(false).error shouldBe null

            game.isOnBattlefield("Ajani Goldmane") shouldBe true
            game.isOnBattlefield("Professor Dellian Fel") shouldBe false
            game.findCardsInLibrary(1, "Professor Dellian Fel").size shouldBe 1
        }
    }
}
