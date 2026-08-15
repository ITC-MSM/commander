package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.RemoveCardType
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.conditions.AllConditions
import com.wingedsheep.sdk.scripting.conditions.SourceCastForImpending
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Add Impending N—[cost] (CR 702.175, Duskmourn: House of Horror).
 *
 * "If you cast this spell for its impending cost, it enters with N time counters and
 * isn't a creature until the last is removed. At the beginning of your end step, remove
 * a time counter from it."
 *
 * Wires the full keyword from one call:
 *  - the [KeywordAbility.Impending] alternative cost (display text + cast enumeration),
 *  - a conditional "isn't a creature while it has a time counter" type-removing static
 *    ability ([RemoveCardType] gated by [Conditions.SourceHasCounter]), and
 *  - a "remove a time counter at the beginning of your end step" triggered ability,
 *    gated by the same intervening-if so it stops once the counters are gone.
 *
 * The engine places [time] TIME counters on the permanent when a spell cast for its
 * impending cost resolves (see the cast/resolve path); these wirings then count it down
 * and keep it a non-creature enchantment until the last counter is removed. Casting the
 * card for its normal mana cost adds no time counters, so neither wiring ever fires and
 * it behaves as an ordinary enchantment creature.
 */
fun CardBuilder.impending(time: Int, cost: String) {
    keywordAbilityList.add(KeywordAbility.Impending(time, ManaCost.parse(cost)))
    // CR 702.176a gates both the "isn't a creature" static and the end-step removal
    // trigger on "impending cost was paid AND has a time counter". The counter check
    // alone is insufficient: any future effect that places time counters on a normally-
    // cast permanent (proliferate-on-counters, ability-granted time counters, etc.)
    // would otherwise turn it into a non-creature.
    val impendingActive = AllConditions(listOf(
        SourceCastForImpending,
        Conditions.SourceHasCounter(
            CounterTypeFilter.Named(Counters.TIME)
        )
    ))
    staticAbilities.add(
        ConditionalStaticAbility(
            ability = RemoveCardType("CREATURE", GroupFilter.source()),
            condition = impendingActive
        )
    )
    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.YourEndStep.event,
            binding = Triggers.YourEndStep.binding,
            effect = Effects.RemoveCounters(Counters.TIME, 1, EffectTarget.Self),
            interveningIf = impendingActive,
            descriptionOverride = "At the beginning of your end step, remove a time counter from this permanent."
        )
    )
}
