package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SpringjackKnight

class SpringjackKnightScenarioTest : FunSpec({
    val boulder = card("Clash Boulder") { manaCost = "{5}"; typeLine = "Artifact" }
    val pebble = card("Clash Pebble") { manaCost = "{0}"; typeLine = "Artifact" }
    fun driver(win: Boolean = true): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(SpringjackKnight, boulder, pebble))
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        putCardOnTopOfLibrary(player1, if (win) "Clash Boulder" else "Clash Pebble")
        putCardOnTopOfLibrary(player2, if (win) "Clash Pebble" else "Clash Boulder")
    }
    fun GameTestDriver.answerClash() {
        repeat(2) {
            val decision = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            submitCardSelection(decision.playerId, emptyList()).error shouldBe null
        }
        pendingDecision shouldBe null
    }
    for (win in listOf(true, false)) {
        test("clash win=$win grants double strike to the chosen creature for this turn") {
            val d = driver(win)
            val knight = d.putCreatureOnBattlefield(d.player1, "Springjack Knight")
            val recipient = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            d.removeSummoningSickness(knight)
            d.passPriorityUntil(Step.DECLARE_ATTACKERS)
            d.declareAttackers(d.player1, listOf(knight), d.player2).error shouldBe null
            d.submitTargetSelection(d.player1, listOf(recipient)).error shouldBe null
            d.bothPass().error shouldBe null
            d.answerClash()
            d.state.projectedState.hasKeyword(recipient, Keyword.DOUBLE_STRIKE) shouldBe win
            d.state.projectedState.hasKeyword(knight, Keyword.DOUBLE_STRIKE) shouldBe false
            d.passPriorityUntil(Step.UPKEEP)
            d.state.projectedState.hasKeyword(recipient, Keyword.DOUBLE_STRIKE) shouldBe false
        }
    }
    test("an illegal creature target stops the trigger before the clash") {
        val d = driver()
        val knight = d.putCreatureOnBattlefield(d.player1, "Springjack Knight")
        val recipient = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.removeSummoningSickness(knight)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(knight), d.player2).error shouldBe null
        d.submitTargetSelection(d.player1, listOf(recipient)).error shouldBe null
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.RED, 1)
        d.castSpell(d.player1, bolt, listOf(recipient)).error shouldBe null
        d.bothPass().error shouldBe null
        (recipient in d.state.getBattlefield()) shouldBe false
        d.bothPass().error shouldBe null
        d.stackSize shouldBe 0
        d.pendingDecision shouldBe null
    }
})
