package com.wingedsheep.engine.handlers.effects.linkedexile

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneMovementUtils
import com.wingedsheep.engine.handlers.effects.ZoneReturnService
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneReturn
import com.wingedsheep.engine.state.components.battlefield.BattlefieldEntryTimestampComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.effects.MoveUntilSourceLeavesEffect
import kotlin.reflect.KClass

class MoveUntilSourceLeavesExecutor : EffectExecutor<MoveUntilSourceLeavesEffect> {
    override val effectType: KClass<MoveUntilSourceLeavesEffect> = MoveUntilSourceLeavesEffect::class

    override fun execute(state: GameState, effect: MoveUntilSourceLeavesEffect, context: EffectContext): EffectResult {
        val sourceId = context.sourceId ?: return EffectResult.success(state)
        // Phasing does not end the source's battlefield visit.
        if (state.logicalZone(sourceId)?.zoneType != Zone.BATTLEFIELD || context.sourceReferenceLost) {
            return EffectResult.success(state)
        }
        val timestamp = state.getEntity(sourceId)?.get<BattlefieldEntryTimestampComponent>()?.timestamp
        if (context.sourceBattlefieldTimestamp != null && context.sourceBattlefieldTimestamp != timestamp) {
            return EffectResult.success(state)
        }
        val source = state.objectRef(sourceId) ?: return EffectResult.success(state)
        val capturedSource = context.objectReferences.source
        if (capturedSource != null && capturedSource != source) return EffectResult.success(state)
        val targetId = context.resolveTarget(effect.target, state) ?: return EffectResult.success(state)
        val previousZone = state.logicalZone(targetId)?.zoneType ?: return EffectResult.success(state)
        if (previousZone == effect.destination) return EffectResult.success(state)
        val result = ZoneTransitionService.moveToZone(state, targetId, effect.destination)
        val moved = result.transitions.firstOrNull { it.oldObject == state.objectRef(targetId) }
            ?.newObject ?: return EffectResult.success(result.state, result.events)
        if (result.actualDestination != effect.destination || !result.state.isCurrentObject(moved)) {
            return EffectResult.success(result.state, result.events)
        }
        // Keep the ordinary linked-exile view for the client and abilities that reference the pile.
        val linked = if (effect.destination == Zone.EXILE && result.state.isCurrentObject(source)) {
            ZoneMovementUtils.linkExiledToSource(result.state, targetId, sourceId)
        } else result.state
        val recorded = linked.copy(zoneReturns = linked.zoneReturns + ZoneReturn(source, moved, previousZone))
        // Moving the source itself can end the duration as part of the initial move.
        val returns = ZoneReturnService.returnDepartedSources(recorded)
        return EffectResult.success(returns.state, result.events + returns.events)
    }
}
