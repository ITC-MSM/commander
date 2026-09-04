package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.FistfulOfForce
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FistfulOfForceScenarioTest : FunSpec({
    val Boulder = card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(win: Boolean): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(FistfulOfForce, Boulder, Pebble))
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
        test("clash win=$win preserves the creature target and limits the bonus to this turn") {
            val d = driver(win)
            val creature = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            val other = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            val spell = d.putCardInHand(d.player1, "Fistful of Force")
            d.giveMana(d.player1, Color.GREEN, 2)
            d.castSpell(d.player1, spell, listOf(creature)).error shouldBe null
            d.bothPass().error shouldBe null

            d.state.projectedState.getPower(creature) shouldBe 4
            d.answerClash()

            d.state.projectedState.getPower(creature) shouldBe if (win) 6 else 4
            d.state.projectedState.getToughness(creature) shouldBe if (win) 6 else 4
            d.state.projectedState.hasKeyword(creature, Keyword.TRAMPLE) shouldBe win
            d.state.projectedState.getPower(other) shouldBe 2
            d.passPriorityUntil(Step.UPKEEP)
            d.state.projectedState.getPower(creature) shouldBe 2
            d.state.projectedState.getToughness(creature) shouldBe 2
            d.state.projectedState.hasKeyword(creature, Keyword.TRAMPLE) shouldBe false
        }
    }
})
