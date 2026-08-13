package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ExecutorModule
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.effects.Effect

/**
 * Module providing zone-transition effect executors — effects that physically move
 * entities between zones (battlefield, graveyard, exile, hand, library) without
 * adding or removing link bookkeeping.
 */
class ZonesExecutors(
    private val cardRegistry: CardRegistry,
    private val targetFinder: TargetFinder = TargetFinder()
) : ExecutorModule {
    /**
     * Zone-return effects can expand to a CompositeEffect.  It is initialized by the registry
     * before this module is registered, just like the library/permanent recursive modules.
     */
    private var recurse: ((GameState, Effect, EffectContext) -> com.wingedsheep.engine.core.EffectResult)? = null

    fun initializeRecursion(runner: (GameState, Effect, EffectContext) -> com.wingedsheep.engine.core.EffectResult) {
        recurse = runner
    }

    override fun executors(): List<EffectExecutor<*>> = listOf(
        MoveToZoneEffectExecutor(cardRegistry, targetFinder),
        ExileAndGrantOwnerPlayPermissionExecutor(),
        WarpExileExecutor(),
        MoveTrackedBattlefieldObjectExecutor(),
        ForceExileMultiZoneExecutor(),
        ForceSacrificeExecutor(),
        SacrificeExecutor(),
        SacrificeSelfExecutor(),
        SacrificeTargetExecutor(),
        EmitExploitedEventExecutor(),
        ReturnCreaturesPutInGraveyardThisTurnExecutor { state, effect, context ->
            requireNotNull(recurse) { "ZonesExecutors recursion has not been initialized" }(state, effect, context)
        },
        ReturnSameNamedFromGraveyardExecutor(),
        ReturnSelfToBattlefieldAttachedExecutor(cardRegistry),
        PutOntoBattlefieldAttachedToChosenExecutor(cardRegistry, targetFinder),
        ExileOpponentsGraveyardsExecutor(),
        DestroyAllEquipmentOnTargetExecutor()
    )
}
