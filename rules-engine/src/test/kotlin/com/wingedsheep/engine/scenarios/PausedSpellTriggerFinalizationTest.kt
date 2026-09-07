package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class PausedSpellTriggerFinalizationTest : FunSpec({
    for (targeted in listOf(false, true)) {
        for (initialDraw in listOf(false, true)) {
            test("triggers between serialized nested choices wait for spell finalization: targeted=$targeted, initialDraw=$initialDraw") {
                val witness = card("Finalization Draw Witness") {
                    manaCost = "{0}"; typeLine = "Enchantment"
                    triggeredAbility {
                        trigger = Triggers.YouDraw
                        if (targeted) target("target spell", Targets.Spell)
                        effect = if (targeted) Effects.CounterSpell() else Effects.GainLife(1)
                    }
                }
                val opposingWitness = card("Finalization Opposing Draw Witness") {
                    manaCost = "{0}"; typeLine = "Enchantment"
                    triggeredAbility { trigger = Triggers.OpponentDraws; effect = Effects.GainLife(1) }
                }
                val spell = card("Finalization Paused Draw") {
                    manaCost = "{0}"; typeLine = "Sorcery"
                    spell {
                        val choices = GatedEffect(Gate.MayDecide("Draw?"), Effects.Composite(
                            Effects.DrawCards(1),
                            GatedEffect(Gate.MayDecide("Gain life?"), Effects.GainLife(2))))
                        effect = if (initialDraw) Effects.Composite(Effects.DrawCards(1), choices) else choices
                    }
                }
                val d = GameTestDriver().apply {
                    registerCards(TestCards.all + listOf(witness, opposingWitness, spell))
                    initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
                    passPriorityUntil(Step.PRECOMBAT_MAIN)
                }
                d.putPermanentOnBattlefield(d.player1, witness.name)
                if (!targeted) d.putPermanentOnBattlefield(d.player2, opposingWitness.name)
                val id = d.putCardInHand(d.player1, spell.name)
                d.castSpell(d.player1, id).error shouldBe null
                d.bothPass().error shouldBe null
                val json = Json { serializersModule = engineSerializersModule; allowStructuredMapKeys = true }
                repeat(2) {
                    d.replaceState(json.decodeFromString(GameState.serializer(),
                        json.encodeToString(GameState.serializer(), d.state)))
                    d.submitYesNo(d.player1, true).error shouldBe null
                }
                d.pendingDecision shouldBe null
                d.state.continuationStack shouldBe emptyList()
                (id in d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD))) shouldBe true
                val drawCount = if (initialDraw) 2 else 1
                d.stackSize shouldBe if (targeted) 0 else drawCount * 2
                if (!targeted) {
                    d.state.stack.map { d.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()?.controllerId } shouldBe
                        List(drawCount) { d.player1 } + List(drawCount) { d.player2 }
                }
                d.events.filterIsInstance<ZoneChangeEvent>().count {
                    it.entityId == id && it.fromZone == Zone.STACK
                } shouldBe 1
            }
        }
    }
})
