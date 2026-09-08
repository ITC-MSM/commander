package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VariableLoyaltyCostTest : FunSpec({
    val walker = card("Variable Loyalty Test") {
        manaCost = "{2}{U}"
        typeLine = "Legendary Planeswalker — Test"
        startingLoyalty = 3
        oracleText = "−X: Draw X cards."
        loyaltyAbilityX { effect = Effects.DrawCards(DynamicAmount.XValue) }
    }
    fun driver() = GameTestDriver().also {
        it.registerCards(TestCards.all + walker)
        it.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        it.passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("variable loyalty survives SDK serialization") {
        val decoded = Json.decodeFromString<CardDefinition>(Json.encodeToString(walker))
        decoded.script.activatedAbilities.single().cost shouldBe walker.script.activatedAbilities.single().cost
    }

    for (x in listOf(0, 2, 3)) {
        test("legal action and number continuation cap X at loyalty and retain X=$x") {
            val d = driver()
            val me = d.activePlayer!!
            val source = d.putPermanentOnBattlefield(me, walker.name).also {
                d.addComponent(it, CountersComponent(mapOf(CounterType.LOYALTY to 3)))
            }
            val action = d.legalActions(me).single {
                (it.action as? ActivateAbility)?.sourceId == source
            }
            action.hasXCost shouldBe true
            action.maxAffordableX shouldBe 3
            action.minX shouldBe 0
            val hand = d.getHandSize(me)
            d.submit(action.action).isPaused shouldBe true
            val question = d.pendingDecision as ChooseNumberDecision
            question.minValue shouldBe 0
            question.maxValue shouldBe 3
            d.submitDecision(me, NumberChosenResponse(question.id, x)).error shouldBe null
            d.bothPass()
            d.getHandSize(me) shouldBe hand + x
            // A variable cost counts as the permanent's loyalty activation this turn.
            d.legalActions(me).any { (it.action as? ActivateAbility)?.sourceId == source } shouldBe false
        }
    }

    test("variable loyalty remains visible with its oracle description in the client menu") {
        val d = driver()
        val me = d.activePlayer!!
        val source = d.putPermanentOnBattlefield(me, walker.name).also {
            d.addComponent(it, CountersComponent(mapOf(CounterType.LOYALTY to 3)))
        }
        val menu = ClientStateTransformer(cardRegistry = d.cardRegistry)
            .transform(d.state, viewingPlayerId = me).cards[source]!!.planeswalkerAbilities!!
        menu.single().loyaltyX shouldBe true
        menu.single().description shouldBe "Draw X cards."
    }

    test("a variable loyalty ability cannot be used on an opponent's turn") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        val source = d.putPermanentOnBattlefield(opp, walker.name)
        d.passPriority(me)
        d.submit(ActivateAbility(opp, source, walker.script.activatedAbilities.single().id, xValue = 0))
            .isSuccess shouldBe false
    }
})
