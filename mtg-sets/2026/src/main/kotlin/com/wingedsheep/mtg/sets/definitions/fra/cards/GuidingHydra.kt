package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Guiding Hydra — the combat trigger has no intervening-if, but "If you do" only follows a counter
 * that was actually removed, so the optional branch is only offered while the Hydra still has a
 * +1/+1 counter at resolution (a Hydra that has left, or has none, removes nothing and spreads
 * nothing).
 */
val GuidingHydra = card("Guiding Hydra") {
    manaCost = "{X}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Hydra Horror"
    power = 1
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it.\n" +
        "At the beginning of combat on your turn, you may remove a +1/+1 counter from this creature. " +
        "If you do, put a +1/+1 counter on each other creature you control."

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = ConditionalEffect(
            condition = Conditions.SourceHasCounter(CounterTypeFilter.PlusOnePlusOne),
            effect = MayEffect(
                Effects.Composite(
                    Effects.RemoveCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                    Effects.ForEachInGroup(
                        GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
                        Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                    ),
                ),
            ),
        )
        description = "At the beginning of combat on your turn, you may remove a +1/+1 counter from " +
            "this creature. If you do, put a +1/+1 counter on each other creature you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Jason Mowry"
        flavorText = "It hands direction to straying minds."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a53eb840-039d-4c45-b701-d58cb26b1a6c.jpg?1789644816"
        inBooster = false
    }
}
