package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class RankRatScenarioTest : ScenarioTestBase() {
    init {
        test("opponent chooses a discard while your hand stays intact") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Rank Rat")
                .withCardInHand(1, "Forest")
                .withCardInHand(2, "Island")
                .withCardInHand(2, "Mountain")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Rank Rat").error shouldBe null
            game.resolveStack()
            game.selectCards(listOf(game.findCardsInHand(2, "Island").single())).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Island") shouldBe true
            game.isInHand(2, "Mountain") shouldBe true
            game.isInHand(1, "Forest") shouldBe true
        }

        test("an opponent with an empty hand does not block resolution") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Rank Rat")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Rank Rat").error shouldBe null
            game.resolveStack()
            game.state.pendingDecision shouldBe null
            (game.findPermanent("Rank Rat") != null) shouldBe true
        }
    }
}
