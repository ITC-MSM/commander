package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Iceberg
 * {X}{U}{U}
 * Enchantment
 *
 * This enchantment enters with X ice counters on it.
 * {3}: Put an ice counter on this enchantment.
 * Remove an ice counter from this enchantment: Add {C}.
 *
 * A mana battery, so the counters are the store and nothing else reads them: `Counters.ICE` is
 * already in the `CounterType` enum, which keeps `CounterTypeFilter.Named` from failing open to
 * +1/+1. The cast-time X reaches the permanent through [EntersWithDynamicCounters] with a
 * [DynamicAmount.XValue] count — the replacement effect runs inside the permanent spell's own
 * resolution, where X is still live. Withdrawals are a plain mana ability whose cost is
 * `Costs.RemoveCounterFromSelf`, so they can be activated while paying for something else.
 */
val Iceberg = card("Iceberg") {
    manaCost = "{X}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "This enchantment enters with X ice counters on it.\n" +
        "{3}: Put an ice counter on this enchantment.\n" +
        "Remove an ice counter from this enchantment: Add {C}."

    replacementEffect(
        EntersWithDynamicCounters(
            counterType = CounterTypeFilter.Named(Counters.ICE),
            count = DynamicAmount.XValue
        )
    )

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.AddCounters(Counters.ICE, 1, EffectTarget.Self)
        description = "{3}: Put an ice counter on this enchantment."
    }

    activatedAbility {
        cost = Costs.RemoveCounterFromSelf(Counters.ICE)
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "Remove an ice counter from this enchantment: Add {C}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Jeff A. Menges"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2f70e49-17fa-4033-bd45-63374f7f5ec5.jpg"
    }
}
