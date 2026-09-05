package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SpringCleaning
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class SpringCleaningScenarioTest : FunSpec({
    val Boulder = card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }
    val Enchantment = card("Clash Test Enchantment") {
        manaCost = "{1}"; typeLine = "Enchantment"; oracleText = ""
    }

    fun driver(win: Boolean): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(SpringCleaning, Boulder, Pebble, Enchantment))
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
        test("clash win=$win destroys the target first and sweeps only opposing enchantments on a win") {
            val d = driver(win)
            val target = d.putPermanentOnBattlefield(d.player1, "Clash Test Enchantment")
            val own = d.putPermanentOnBattlefield(d.player1, "Clash Test Enchantment")
            val opposing = d.putPermanentOnBattlefield(d.player2, "Clash Test Enchantment")
            val opposingOther = d.putPermanentOnBattlefield(d.player2, "Clash Test Enchantment")
            val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
            val spell = d.putCardInHand(d.player1, "Spring Cleaning")
            d.giveMana(d.player1, Color.GREEN, 2)
            d.castSpell(d.player1, spell, listOf(target)).error shouldBe null
            d.bothPass().error shouldBe null

            d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD)).contains(target) shouldBe true
            d.answerClash()

            d.state.getBattlefield().contains(own) shouldBe true
            d.state.getBattlefield().contains(creature) shouldBe true
            d.state.getBattlefield().contains(opposing) shouldBe !win
            d.state.getBattlefield().contains(opposingOther) shouldBe !win
        }
    }
})
