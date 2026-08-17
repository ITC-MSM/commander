package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ExecutorModule
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.effects.Effect
import java.util.concurrent.atomic.AtomicReference

/**
 * Module providing zone-transition effect executors — effects that physically move
 * entities between zones (battlefield, graveyard, exile, hand, library) without
 * adding or removing link bookkeeping.
 */
class ZonesExecutors(
    private val cardRegistry: CardRegistry,
    private val targetFinder: TargetFinder = TargetFinder()
) : ExecutorModule {

    // Late-bound registry recursion so [MoveToZoneEffectExecutor] can run an entering permanent's
    // OnEnterRunEffect replacement ("as this enters, …") when an effect puts a card onto the
    // battlefield. Mirrors PermanentExecutors' wiring: read through the ref at execution time, so
    // constructing this module before initialization (as some unit tests do) never trips over an
    // unset property.
    private val recursionRef =
        AtomicReference<((GameState, Effect, EffectContext) -> EffectResult)?>(null)

    private val recursion: (GameState, Effect, EffectContext) -> EffectResult = { state, effect, context ->
        val executor = recursionRef.get()
            ?: error("ZonesExecutors.initializeRecursion(...) was not called before a zone executor ran")
        executor(state, effect, context)
    }

    /** Late-bind the registry's recursive executor so the zone executors can delegate. */
    fun initializeRecursion(executor: (GameState, Effect, EffectContext) -> EffectResult) {
        recursionRef.set(executor)
    }

    override fun executors(): List<EffectExecutor<*>> = listOf(
        MoveToZoneEffectExecutor(cardRegistry, targetFinder, recursion),
        ExileAndGrantOwnerPlayPermissionExecutor(),
        WarpExileExecutor(),
        MoveTrackedBattlefieldObjectExecutor(),
        ForceExileMultiZoneExecutor(),
        ForceSacrificeExecutor(),
        SacrificeExecutor(),
        SacrificeSelfExecutor(),
        SacrificeTargetExecutor(),
        EmitExploitedEventExecutor(),
        ReturnCreaturesPutInGraveyardThisTurnExecutor(),
        ReturnSameNamedFromGraveyardExecutor(),
        ReturnSelfToBattlefieldAttachedExecutor(cardRegistry),
        PutOntoBattlefieldAttachedToChosenExecutor(cardRegistry, targetFinder),
        ExileOpponentsGraveyardsExecutor(),
        DestroyAllEquipmentOnTargetExecutor()
    )
}
