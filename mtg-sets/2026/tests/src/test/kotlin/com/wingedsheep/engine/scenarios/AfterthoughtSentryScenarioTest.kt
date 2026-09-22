package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class AfterthoughtSentryScenarioTest : ScenarioTestBase() {
    init {
        listOf(1, 2).forEach { owner ->
            test("attack can exile a card from player $owner's graveyard") {
                val game = scenario().withPlayers()
                    .withCardOnBattlefield(1, "Afterthought Sentry")
                    .withCardInGraveyard(owner, "Forest")
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS).build()
                val card = game.state.getGraveyard(if (owner == 1) game.player1Id else game.player2Id).single()
                game.declareAttackers(mapOf("Afterthought Sentry" to 2)).error shouldBe null
                game.selectTargets(listOf(card)).error shouldBe null
                game.resolveStack()
                game.isInGraveyard(owner, "Forest") shouldBe false
                game.state.getExile(if (owner == 1) game.player1Id else game.player2Id).contains(card) shouldBe true
            }
        }

        test("attack can choose no graveyard target") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Afterthought Sentry")
                .withCardInGraveyard(2, "Forest")
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS).build()
            game.declareAttackers(mapOf("Afterthought Sentry" to 2)).error shouldBe null
            game.selectTargets(emptyList()).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Forest") shouldBe true
        }
    }
}
