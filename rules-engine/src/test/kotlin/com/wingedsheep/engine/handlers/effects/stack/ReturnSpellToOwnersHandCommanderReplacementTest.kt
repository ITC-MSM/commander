package com.wingedsheep.engine.handlers.effects.stack

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ReturnSpellToOwnersHandEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * A commander spell returned from the stack is a hand-zone move, so its owner may apply
 * the optional Commander replacement before the spell leaves the stack (CR 903.9b).
 */
class ReturnSpellToOwnersHandCommanderReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commanderSpell = EntityId.generate()

    fun state() = GameState(format = Format.Commander(), turnOrder = listOf(owner))
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(
            commanderSpell,
            ComponentContainer.of(
                CardComponent(
                    cardDefinitionId = "Test Commander",
                    name = "Test Commander",
                    manaCost = ManaCost.ZERO,
                    typeLine = TypeLine(
                        supertypes = setOf(Supertype.LEGENDARY),
                        cardTypes = setOf(CardType.CREATURE)
                    ),
                    ownerId = owner
                ),
                OwnerComponent(owner),
                CommanderComponent(owner),
                SpellOnStackComponent(casterId = owner)
            )
        )
        .pushToStack(commanderSpell)

    fun execute(initial: GameState) = services.effectExecutorRegistry.execute(
        initial,
        ReturnSpellToOwnersHandEffect,
        EffectContext(
            sourceId = null,
            controllerId = owner,
            targets = listOf(ChosenTarget.Spell(commanderSpell))
        )
    )

    fun resume(state: GameState, accept: Boolean) = state.pendingDecision
        .shouldBeInstanceOf<YesNoDecision>()
        .let { decision ->
            services.continuationHandler.resume(
                state.clearPendingDecision(),
                YesNoResponse(decision.id, accept)
            )
        }

    test("accepting the stack-to-hand commander replacement emits exactly one stack-to-command event") {
        val initial = state()
        val paused = execute(initial)

        paused.isPaused shouldBe true
        paused.state.stack.shouldContainExactly(commanderSpell)

        val resolved = resume(paused.state, accept = true)
        resolved.state.stack shouldBe emptyList()
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commanderSpell)
        resolved.state.getZone(ZoneKey(owner, Zone.HAND)) shouldBe emptyList()
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().let { event ->
            event.fromZone shouldBe Zone.STACK
            event.toZone shouldBe Zone.COMMAND
        }
        resolved.state.getEntity(commanderSpell)!!.has<SpellOnStackComponent>() shouldBe false
    }

    test("declining the stack-to-hand commander replacement emits exactly one stack-to-hand event") {
        val paused = execute(state())
        val resolved = resume(paused.state, accept = false)

        resolved.state.stack shouldBe emptyList()
        resolved.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commanderSpell)
        resolved.state.getZone(ZoneKey(owner, Zone.COMMAND)) shouldBe emptyList()
        resolved.events.filterIsInstance<ZoneChangeEvent>().also { it.shouldHaveSize(1) }.single().let { event ->
            event.fromZone shouldBe Zone.STACK
            event.toZone shouldBe Zone.HAND
        }
        resolved.state.getEntity(commanderSpell)!!.has<SpellOnStackComponent>() shouldBe false
    }
})
