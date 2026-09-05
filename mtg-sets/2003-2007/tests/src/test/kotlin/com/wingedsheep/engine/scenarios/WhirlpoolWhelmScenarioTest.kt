package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WhirlpoolWhelm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class WhirlpoolWhelmScenarioTest : FunSpec({
    val boulder = card("Whelm Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(WhirlpoolWhelm, boulder))
        initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    for (win in listOf(true, false)) {
        for (top in if (win) listOf(true, false) else listOf(false)) {
            test("win=$win top=$top chooses destination only after both players finish the clash") {
                val d = driver()
                val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
                d.putCardOnTopOfLibrary(d.player1, if (win) "Whelm Boulder" else "Plains")
                val revealed = d.putCardOnTopOfLibrary(d.player2, if (win) "Plains" else "Whelm Boulder")
                val spell = d.putCardInHand(d.player1, "Whirlpool Whelm")
                d.giveMana(d.player1, Color.BLUE, 2)
                d.castSpell(d.player1, spell, listOf(creature)).error shouldBe null
                d.pendingDecision shouldBe null
                d.bothPass().error shouldBe null
                repeat(2) {
                    val decision = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
                    d.state.getZone(ZoneKey(d.player2, Zone.BATTLEFIELD)).contains(creature) shouldBe true
                    d.submitCardSelection(decision.playerId, if (decision.playerId == d.player2) listOf(revealed) else emptyList()).error shouldBe null
                }
                d.state.getZone(ZoneKey(d.player2, Zone.LIBRARY)).last() shouldBe revealed
                if (win) {
                    d.pendingDecision.shouldBeInstanceOf<YesNoDecision>().playerId shouldBe d.player1
                    d.submitYesNo(d.player1, top).error shouldBe null
                }
                d.pendingDecision shouldBe null
                if (top) d.state.getZone(ZoneKey(d.player2, Zone.LIBRARY)).first() shouldBe creature
                else d.state.getZone(ZoneKey(d.player2, Zone.HAND)).contains(creature) shouldBe true
            }
        }
    }

    test("an illegal target prevents the entire spell including clash") {
        val d = driver()
        val creature = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val spell = d.putCardInHand(d.player1, "Whirlpool Whelm")
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.BLUE, 2)
        d.giveMana(d.player1, Color.RED, 1)
        d.castSpell(d.player1, spell, listOf(creature)).error shouldBe null
        d.castSpell(d.player1, bolt, listOf(creature)).error shouldBe null
        d.bothPass().error shouldBe null
        d.bothPass().error shouldBe null
        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player2, Zone.GRAVEYARD)).contains(creature) shouldBe true
        d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD)).contains(spell) shouldBe true
    }
})
