package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.engine.state.Component
import com.wingedsheep.engine.state.components.battlefield.DamageDealtThisTurnComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DamageDealtThisTurnTest : FunSpec({
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }
    fun GameTestDriver.dealt(source: EntityId): Int = DynamicAmountEvaluator().evaluate(
        state,
        DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.DamageDealtThisTurn),
        EffectContext(sourceId = source, controllerId = player1)
    )

    test("actual damage accumulates across recipients without mutating the previous state") {
        val d = driver()
        val source = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val victim = d.putCreatureOnBattlefield(d.player2, "Centaur Courser")
        val before = d.state
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, victim, 2, source).state)
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, d.player2, 3, source).state)
        d.dealt(source) shouldBe 5
        val json = Json { serializersModule = engineSerializersModule }
        val tracker: Component = d.state.getEntity(source)!!.get<DamageDealtThisTurnComponent>()!!
        json.decodeFromString<Component>(json.encodeToString(tracker)) shouldBe tracker
        val after = d.state
        d.replaceState(before)
        d.dealt(source) shouldBe 0
        d.replaceState(after.copy(turnNumber = after.turnNumber + 1))
        d.dealt(source) shouldBe 0
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, d.player2, 1, source).state)
        d.dealt(source) shouldBe 1
    }

    test("changing zones clears the object's damage history") {
        val d = driver()
        val source = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, d.player2, 2, source).state)
        d.dealt(source) shouldBe 2
        d.moveToGraveyard(source)
        d.dealt(source) shouldBe 0
        // A pending ability's departed source must not mark the new card in the graveyard.
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, d.player2, 1, source).state)
        d.dealt(source) shouldBe 0
    }

    test("combat damage contributes to the same total as noncombat damage") {
        val d = driver()
        val source = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.removeSummoningSickness(source)
        d.replaceState(DamageUtils.dealDamageToTarget(d.state, d.player2, 1, source).state)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(source), d.player2).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(d.player2, emptyMap()).error shouldBe null
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        d.dealt(source) shouldBe 4
    }
    test("trample counts damage to the blocker and player exactly once") {
        val d = driver()
        val source = d.putCreatureOnBattlefield(d.player1, "Trample Beast")
        val blocker = d.putCreatureOnBattlefield(d.player2, "Centaur Courser")
        d.removeSummoningSickness(source)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(source), d.player2).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(d.player2, mapOf(blocker to listOf(source))).error shouldBe null
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        d.dealt(source) shouldBe 5
        d.getLifeTotal(d.player2) shouldBe 18
    }

})
