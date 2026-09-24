package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gardenize — Reality Fracture #103
 * {1}{G}{G} · Enchantment
 *
 * The Altar of Shadows shape: a death trigger feeds charge counters onto the enchantment, and a
 * [Triggers.FirstMainPhase] trigger adds {G} per counter, re-reading the count on resolution.
 */
val Gardenize = card("Gardenize") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control dies, put a charge counter on this enchantment.\n" +
        "At the beginning of your first main phase, add {G} for each charge counter on this enchantment."

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        description = "Whenever a creature you control dies, put a charge counter on this enchantment."
    }

    triggeredAbility {
        trigger = Triggers.FirstMainPhase
        effect = Effects.AddMana(
            Color.GREEN,
            DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE))
        )
        description = "At the beginning of your first main phase, add {G} for each charge counter on this enchantment."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "103"
        artist = "Néstor Ossandón Leal"
        flavorText = "Starting sprouts\n—Vigorbloom term for their first graft"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/930b89c3-4433-48de-829f-20fc3dbfced9.jpg?1789644837"
        inBooster = false
    }
}
