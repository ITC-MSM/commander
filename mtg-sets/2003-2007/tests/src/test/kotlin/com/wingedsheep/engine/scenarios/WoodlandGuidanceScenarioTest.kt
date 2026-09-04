package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WoodlandGuidance
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class WoodlandGuidanceScenarioTest : FunSpec({
    val Boulder = card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(win: Boolean): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(WoodlandGuidance, Boulder, Pebble))
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
        test("clash win=$win always returns the card and exiles the spell but only a win untaps own Forests") {
            val d = driver(win)
            val target = d.putCardInGraveyard(d.player1, "Grizzly Bears")
            val forest = d.putPermanentOnBattlefield(d.player1, "Forest")
            val plains = d.putPermanentOnBattlefield(d.player1, "Plains")
            val theirs = d.putPermanentOnBattlefield(d.player2, "Forest")
            listOf(forest, plains, theirs).forEach(d::tapPermanent)
            val spell = d.putCardInHand(d.player1, "Woodland Guidance")
            d.giveMana(d.player1, Color.GREEN, 4)
            d.castSpellWithTargets(d.player1, spell,
                listOf(ChosenTarget.Card(target, d.player1, Zone.GRAVEYARD))).error shouldBe null
            d.bothPass().error shouldBe null

            d.state.getZone(ZoneKey(d.player1, Zone.HAND)).contains(target) shouldBe true
            d.answerClash()

            d.isTapped(forest) shouldBe !win
            d.isTapped(plains) shouldBe true
            d.isTapped(theirs) shouldBe true
            d.state.getZone(ZoneKey(d.player1, Zone.EXILE)).contains(spell) shouldBe true
        }
    }

    test("opponent's graveyard is not a legal target") {
        val d = driver(true)
        val target = d.putCardInGraveyard(d.player2, "Grizzly Bears")
        val spell = d.putCardInHand(d.player1, "Woodland Guidance")
        d.giveMana(d.player1, Color.GREEN, 4)
        val result = d.castSpellWithTargets(d.player1, spell,
            listOf(ChosenTarget.Card(target, d.player2, Zone.GRAVEYARD)))
        (result.error != null) shouldBe true
    }
})
