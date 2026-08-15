package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Excalibur II
 * {1}
 * Legendary Artifact — Equipment
 * Whenever you gain life, put a charge counter on Excalibur II.
 * Equipped creature gets +1/+1 for each charge counter on Excalibur II.
 * Equip {3}
 *
 * Composed from existing primitives — the Withering Hex shape (an attachment whose grant
 * scales with counters on the attachment itself), inverted to a positive buff:
 *   - A [Triggers.YouGainLife] trigger adds a charge counter to the Equipment
 *     ([EffectTarget.Self]). Per Scryfall rulings, each life-gaining *event* triggers this
 *     once regardless of how much life it represents, which is exactly how YouGainLife fires.
 *   - A [GrantDynamicStatsEffect] (Layer 7c bonus) on [GroupFilter.attachedCreature] reads
 *     the live charge-counter count off the source via
 *     [EntityReference.Source] + [EntityNumericProperty.CounterCount], so the bonus tracks
 *     the counter total continuously.
 */
val ExcaliburII = card("Excalibur II") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Whenever you gain life, put a charge counter on Excalibur II.\n" +
        "Equipped creature gets +1/+1 for each charge counter on Excalibur II.\n" +
        "Equip {3}"

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = AddCountersEffect(Counters.CHARGE, 1, EffectTarget.Self)
    }

    staticAbility {
        val chargeCounters = DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.CounterCount(CounterTypeFilter.Named(Counters.CHARGE))
        )
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = chargeCounters,
            toughnessBonus = chargeCounters
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Russell Dongjun Lu"
        flavorText = "The ultimate sword, used by a legendary king. It was forged in another world."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d42e5fed-67ac-46d7-a5d4-78f661f3e8b4.jpg?1748706751"
    }
}
