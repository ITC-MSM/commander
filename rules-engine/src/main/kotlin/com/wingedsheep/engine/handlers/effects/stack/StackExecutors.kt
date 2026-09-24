package com.wingedsheep.engine.handlers.effects.stack

import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ExecutorModule
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.registry.CardRegistry

/**
 * Module providing all stack-related effect executors.
 */
class StackExecutors(
    private val zones: ZoneTransitionService,
    private val amountEvaluator: DynamicAmountEvaluator = DynamicAmountEvaluator(),
    private val cardRegistry: CardRegistry
) : ExecutorModule {
    override fun executors(): List<EffectExecutor<*>> = listOf(
        CounterEffectExecutor(zones, amountEvaluator, cardRegistry),
        ExileTargetSpellExecutor(zones, cardRegistry),
        ExileSpellsOnStackExecutor(zones, cardRegistry),
        CounterAllOnStackExecutor(zones, cardRegistry),
        WardCounterEffectExecutor(zones, cardRegistry),
        ChangeSpellTargetExecutor(),
        ChangeTargetExecutor(),
        StormCopyEffectExecutor(zones, cardRegistry),
        CopyTargetSpellExecutor(zones, cardRegistry),
        CopyEachTargetSpellExecutor(zones, cardRegistry),
        CopySpellForEachOtherPossibleTargetExecutor(zones, cardRegistry),
        CopyTargetTriggeredAbilityExecutor(zones, cardRegistry),
        CopyTargetSpellOrAbilityExecutor(zones, cardRegistry),
        CopyNextSpellCastExecutor(),
        CopyEachSpellCastExecutor(),
        MakeNextSpellUncounterableExecutor(),
        GrantNextSpellAffinityExecutor(),
        GrantNextSpellFreeCastExecutor(),
        ReduceSpellCostsThisTurnExecutor(amountEvaluator),
        ReselectTargetRandomlyExecutor(),
        ChangeTriggeringObjectTargetsExecutor(),
        GrantKeywordToSpellExecutor(),
        MarkSpellExileWithCountersExecutor(),
        MarkSpellPlotOnResolveExecutor(),
        ReturnSpellToOwnersHandExecutor(),
        ReturnSpellOrPermanentToOwnersHandExecutor(zones, cardRegistry),
        DestroySourceOfTargetedAbilityExecutor(zones),
        RemoveAbilitiesFromSourceOfTargetedAbilityExecutor()
    )
}
