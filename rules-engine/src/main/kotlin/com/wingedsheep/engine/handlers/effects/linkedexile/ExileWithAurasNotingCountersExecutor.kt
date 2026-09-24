package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneEntryOptions
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.battlefield.NotedExileComponent
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ExileWithAurasNotingCountersEffect
import kotlin.reflect.KClass

/**
 * Executor for [ExileWithAurasNotingCountersEffect] (Tawnos's Coffin's activated ability).
 *
 * Exiles the target creature and every Aura attached to it, linking all of them to the source
 * permanent via [LinkedExileComponent], and records the creature's counters plus its identity on
 * the source via [NotedExileComponent]. The attached Auras are captured *before* the creature is
 * exiled — once the creature leaves, the unattached-Aura SBA would otherwise send them to the
 * graveyard.
 *
 * The return is handled by [ReturnNotedExileTappedWithAurasExecutor], fired from the source's
 * leaves-the-battlefield and becomes-untapped triggers.
 */
class ExileWithAurasNotingCountersExecutor(private val zones: ZoneTransitionService) : EffectExecutor<ExileWithAurasNotingCountersEffect> {

    override val effectType: KClass<ExileWithAurasNotingCountersEffect> =
        ExileWithAurasNotingCountersEffect::class

    override fun execute(
        state: GameState,
        effect: ExileWithAurasNotingCountersEffect,
        context: EffectContext
    ): EffectResult {
        val sourceId = context.sourceId ?: return EffectResult.success(state)

        // Modern template: if the source already left the battlefield, do nothing.
        if (sourceId !in state.getBattlefield()) return EffectResult.success(state)

        val creatureId = context.resolveTarget(effect.target) ?: return EffectResult.success(state)
        if (creatureId !in state.getBattlefield()) return EffectResult.success(state)

        // Capture the Auras attached to the creature (before it leaves the battlefield). Read the
        // Aura subtype through projection — a text-changing / type-changing effect could alter it.
        val projected = state.projectedState
        val auraIds = state.getEntity(creatureId)?.get<AttachmentsComponent>()?.attachedIds
            ?.filter { attachId ->
                attachId in state.getBattlefield() &&
                    projected.hasSubtype(attachId, Subtype.AURA.value)
            }
            ?: emptyList()

        // Note the number and kind of counters on the creature.
        val notedCounters = state.getEntity(creatureId)?.get<CountersComponent>()?.counters
            ?.filterValues { it > 0 }
            ?: emptyMap()

        val events = mutableListOf<com.wingedsheep.engine.core.GameEvent>()
        var newState = state
        val exiledIds = mutableListOf<EntityId>()

        // Exile the creature, then its Auras. Auras after the creature so the creature is the
        // principal recorded first.
        for (id in listOf(creatureId) + auraIds) {
            if (id !in newState.getBattlefield()) continue
            val transition = zones.moveToZone(
                newState, id, Zone.EXILE, ZoneEntryOptions(skipZoneChangeRedirect = true)
            )
            newState = transition.state
            events.addAll(transition.events)
            exiledIds.add(id)
        }

        if (exiledIds.isEmpty() || creatureId !in exiledIds) return EffectResult.success(newState, events)

        // Link every exiled card to the source, and record the noted-exile snapshot.
        newState = newState.updateEntity(sourceId) { c ->
            val existingLinked = c.get<LinkedExileComponent>()?.exiledIds ?: emptyList()
            c.with(LinkedExileComponent(existingLinked + exiledIds))
                .with(NotedExileComponent(principalId = creatureId, notedCounters = notedCounters))
        }

        return EffectResult.success(newState, events)
    }
}
