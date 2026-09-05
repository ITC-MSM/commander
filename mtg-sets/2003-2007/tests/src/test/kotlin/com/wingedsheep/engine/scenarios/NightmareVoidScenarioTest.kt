package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class NightmareVoidScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Nightmare Void")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Nightmare Void"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Nightmare Void") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 2
            game.state.getLibrary(game.player1Id).size shouldBe 3
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("caster chooses which card the targeted opponent discards") {
            val game = base().withCardInHand(1, "Nightmare Void")
                .withLandsOnBattlefield(1, "Swamp", 4)
                .withCardInHand(2, "Forest").withCardInHand(2, "Grizzly Bears").build()
            game.castSpellTargetingPlayer(1, "Nightmare Void", 2).error shouldBe null
            game.resolveStack()
            val decision = game.state.pendingDecision as SelectCardsDecision
            decision.playerId shouldBe game.player1Id
            val bear = game.state.getHand(game.player2Id).first { game.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Grizzly Bears" }
            game.selectCards(listOf(bear)).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.isInHand(2, "Forest") shouldBe true
        }

        test("self targeting reveals and discards from your own hand") {
            val game = base().withCardInHand(1, "Nightmare Void")
                .withCardInHand(1, "Forest").withCardInHand(1, "Island")
                .withLandsOnBattlefield(1, "Swamp", 4).build()
            game.castSpellTargetingPlayer(1, "Nightmare Void", 1).error shouldBe null
            game.resolveStack()
            val decision = game.state.pendingDecision as SelectCardsDecision
            decision.playerId shouldBe game.player1Id
            game.selectCards(listOf(game.state.getHand(game.player1Id).first())).error shouldBe null
            game.resolveStack()
            game.state.getHand(game.player1Id).size shouldBe 1
        }

        test("empty hand resolves without requiring a card choice") {
            val game = base().withCardInHand(1, "Nightmare Void")
                .withLandsOnBattlefield(1, "Swamp", 4).build()
            game.castSpellTargetingPlayer(1, "Nightmare Void", 2).error shouldBe null
            game.resolveStack()
            game.state.pendingDecision shouldBe null
            game.isInGraveyard(1, "Nightmare Void") shouldBe true
        }
    }
}
