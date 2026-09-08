package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.DependentTargetSelection
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DependentTargetSelectionTest : FunSpec({
    val cards = TestCards.all
    fun driver() = GameTestDriver().also {
        it.registerCards(cards)
        it.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true)
    }

    test("a paused dependent selection preserves its chosen prefix through serialization") {
        val d = driver()
        val chosen = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val frame = com.wingedsheep.engine.core.TriggeredAbilityContinuation(
            sourceId = chosen,
            sourceName = "Test source",
            controllerId = d.player1,
            effect = com.wingedsheep.sdk.dsl.Effects.DrawCards(1),
            description = "Test dependent targets",
            sequentialTargets = listOf(chosen),
        )
        val json = kotlinx.serialization.json.Json {
            serializersModule = com.wingedsheep.engine.core.engineSerializersModule
            allowStructuredMapKeys = true
        }
        val serializer = com.wingedsheep.engine.core.TriggeredAbilityContinuation.serializer()
        json.decodeFromString(serializer, json.encodeToString(serializer, frame)) shouldBe frame
    }

    test("a color-relative filter offers only first choices with a compatible partner") {
        val d = driver()
        val green = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        val partner = d.putCreatureOnBattlefield(d.player2, "Llanowar Elves")
        val requirements = listOf(
            TargetCreature(filter = TargetFilter.CreatureYouControl),
            TargetCreature(filter = TargetFilter(com.wingedsheep.sdk.scripting.GameObjectFilter.Creature.opponentControls().sharingColorWith(EntityReference.Target(0)))),
        )
        val context = PredicateContext(controllerId = d.player1)
        DependentTargetSelection.isRequired(requirements) shouldBe true
        DependentTargetSelection.legalNext(d.state, requirements, emptyList(), context) shouldBe listOf(green)
        DependentTargetSelection.legalNext(d.state, requirements, listOf(green), context) shouldBe listOf(partner)
    }

    test("lookahead checks all remaining slots rather than only the next slot") {
        val d = driver()
        val small = d.putCreatureOnBattlefield(d.player1, "Llanowar Elves")
        val large = d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        val middle = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val requirements = listOf(
            TargetCreature(filter = TargetFilter.CreatureYouControl),
            TargetCreature(filter = TargetFilter.CreatureOpponentControls.powerLessThanEntity(EntityReference.Target(0))),
            TargetCreature(filter = TargetFilter.CreatureYouControl.powerLessThanEntity(EntityReference.Target(1))),
        )
        val context = PredicateContext(controllerId = d.player1)
        DependentTargetSelection.legalNext(d.state, requirements, emptyList(), context) shouldBe listOf(large)
        DependentTargetSelection.legalNext(d.state, requirements, listOf(large), context) shouldBe listOf(middle)
        DependentTargetSelection.legalNext(d.state, requirements, listOf(large, middle), context) shouldBe listOf(small)
    }
})
