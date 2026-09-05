package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HoardersGreed
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class HoardersGreedScenarioTest : FunSpec({
    val boulder = card("Greed Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }
    val pebble = card("Greed Pebble") { manaCost = "{1}"; typeLine = "Artifact"; oracleText = "" }
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(HoardersGreed, boulder, pebble))
        initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }
    fun GameTestDriver.clash() {
        repeat(2) {
            val decision = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            submitCardSelection(decision.playerId, emptyList()).error shouldBe null
        }
    }
    for (firstWin in listOf(true, false)) {
        for (tie in listOf(true, false)) {
            test("first win=$firstWin automatically repeats then stops on ${if (tie) "tie" else "loss"}") {
                val d = driver()
                // Top-first order: draw two, clash, then (on a win) draw that reveal and another card.
                val library = if (firstWin) listOf("Plains", "Plains", "Greed Boulder", "Plains", if (tie) "Greed Pebble" else "Plains")
                    else listOf("Plains", "Plains", if (tie) "Greed Pebble" else "Plains")
                library.reversed().forEach { d.putCardOnTopOfLibrary(d.player1, it) }
                d.putCardOnTopOfLibrary(d.player2, "Greed Pebble")
                val spell = d.putCardInHand(d.player1, "Hoarder's Greed")
                d.giveMana(d.player1, Color.BLACK, 4)
                d.castSpell(d.player1, spell).error shouldBe null
                val initialHand = d.state.getZone(ZoneKey(d.player1, Zone.HAND)).size
                d.bothPass().error shouldBe null
                d.getLifeTotal(d.player1) shouldBe 18
                d.state.getZone(ZoneKey(d.player1, Zone.HAND)).size shouldBe initialHand + 2
                d.clash()
                if (firstWin) {
                    // The next clash prompt follows immediately, with no optional-repeat question.
                    d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
                    d.getLifeTotal(d.player1) shouldBe 16
                    d.state.getZone(ZoneKey(d.player1, Zone.HAND)).size shouldBe initialHand + 4
                    d.clash()
                }
                d.pendingDecision shouldBe null
                d.getLifeTotal(d.player1) shouldBe if (firstWin) 16 else 18
                d.getLifeTotal(d.player2) shouldBe 20
                d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD)).contains(spell) shouldBe true
            }
        }
    }
})
