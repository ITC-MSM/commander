package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GiltLeafAmbush
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GiltLeafAmbushScenarioTest : FunSpec({
    val boulder = card("Ambush Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }
    for ((win, doubled) in listOf(true to false, false to false, true to true)) {
        test("win=$win doubled=$doubled grants temporary deathtouch to exactly the created tokens") {
            val d = GameTestDriver().apply {
                registerCards(TestCards.all + listOf(GiltLeafAmbush, boulder))
                initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
                passPriorityUntil(Step.PRECOMBAT_MAIN)
            }
            val otherCreature = d.putCreatureOnBattlefield(d.player1, "Llanowar Elves")
            if (doubled) d.putPermanentOnBattlefield(d.player1, "Doubling Season")
            val before = d.state.getZone(ZoneKey(d.player1, Zone.BATTLEFIELD)).toSet()
            d.putCardOnTopOfLibrary(d.player1, if (win) "Ambush Boulder" else "Plains")
            d.putCardOnTopOfLibrary(d.player2, if (win) "Plains" else "Ambush Boulder")
            val spell = d.putCardInHand(d.player1, "Gilt-Leaf Ambush")
            d.giveMana(d.player1, Color.GREEN, 3)
            d.castSpell(d.player1, spell).error shouldBe null
            d.bothPass().error shouldBe null

            // Creation precedes the clash, and token doubling contributes every new token.
            val tokens = d.state.getZone(ZoneKey(d.player1, Zone.BATTLEFIELD)).filter { it !in before }
            tokens.size shouldBe if (doubled) 4 else 2
            tokens.forEach {
                d.state.projectedState.getPower(it) shouldBe 1
                d.state.projectedState.getToughness(it) shouldBe 1
                d.state.projectedState.hasKeyword(it, Keyword.DEATHTOUCH) shouldBe false
            }
            repeat(2) {
                val decision = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
                d.submitCardSelection(decision.playerId, emptyList()).error shouldBe null
            }
            d.pendingDecision shouldBe null
            tokens.forEach { d.state.projectedState.hasKeyword(it, Keyword.DEATHTOUCH) shouldBe win }
            d.state.projectedState.hasKeyword(otherCreature, Keyword.DEATHTOUCH) shouldBe false
            d.passPriorityUntil(Step.UPKEEP)
            tokens.forEach { d.state.projectedState.hasKeyword(it, Keyword.DEATHTOUCH) shouldBe false }
        }
    }
})
