package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class MindleechMassScenarioTest : ScenarioTestBase() {
    init {
        fun combat(hand: List<String> = listOf("Grizzly Bears", "Island")): TestGame {
            val setup = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Mindleech Mass", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            hand.forEach { setup.withCardInHand(2, it) }
            val game = setup.build()
            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Mindleech Mass" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
            game.resolveStack()
            if (game.state.pendingDecision is CombatResolutionDecision) {
                game.submitDefaultCombatDamage()
                game.resolveStack()
            }
            (game.state.pendingDecision is YesNoDecision) shouldBe true
            return game
        }

        test("casts an opponent's creature during combat for free under your control") {
            val game = combat()
            val bears = game.findCardsInHand(2, "Grizzly Bears").single()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision is SelectCardsDecision) shouldBe true
            game.selectCards(listOf(bears)).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Grizzly Bears") shouldBe false
            game.state.projectedState.getController(bears) shouldBe game.player1Id
            game.isInHand(2, "Island") shouldBe true
        }

        test("casts a targeted spell and sends it to its owner's graveyard") {
            val game = combat(listOf("Lightning Bolt"))
            val bolt = game.findCardsInHand(2, "Lightning Bolt").single()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.selectCards(listOf(bolt)).error shouldBe null
            game.selectTargets(listOf(game.player2Id)).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 11
            game.isInGraveyard(2, "Lightning Bolt") shouldBe true
        }

        test("a land cannot be selected as a spell") {
            val game = combat()
            val island = game.findCardsInHand(2, "Island").single()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.selectCards(listOf(island)).isSuccess shouldBe false
            game.isInHand(2, "Island") shouldBe true
        }

        for (hand in listOf(emptyList(), listOf("Island"))) {
            test("hand with no spells completes without a cast: $hand") {
                val game = combat(hand)
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()
                if (hand.isNotEmpty()) {
                    game.selectCards(emptyList()).error shouldBe null
                    game.resolveStack()
                }
                game.state.pendingDecision shouldBe null
                game.state.getHand(game.player2Id).size shouldBe hand.size
            }
        }

        test("may look at the hand and decline to cast") {
            val game = combat()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.selectCards(emptyList()).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Grizzly Bears") shouldBe true
            game.state.pendingDecision shouldBe null
        }

        test("may decline to look at the hand") {
            val game = combat()
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()
            game.isInHand(2, "Grizzly Bears") shouldBe true
            game.state.pendingDecision shouldBe null
        }
    }
}
