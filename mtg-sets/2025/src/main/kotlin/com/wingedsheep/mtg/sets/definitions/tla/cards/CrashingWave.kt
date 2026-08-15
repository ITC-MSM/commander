package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Crashing Wave
 * {U}{U}
 * Sorcery
 * As an additional cost to cast this spell, waterbend {X}.
 * Tap up to X target creatures, then distribute three stun counters among any number of tapped
 * creatures your opponents control.
 *
 * X comes from the waterbend {X} cost (`waterbendCost(isX = true)`): the chosen X bounds the
 * targeting (`dynamicMaxCount = DynamicAmount.XValue`) and is the waterbend amount paid by tapping
 * artifacts/creatures. Resolution: tap the up-to-X target creatures, then distribute three stun
 * counters among the tapped creatures opponents control (resolved at that point, so it includes any
 * just tapped) — `minPerTarget = 0`, so all three may pile onto one.
 */
val CrashingWave = card("Crashing Wave") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, waterbend {X}. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)\n" +
        "Tap up to X target creatures, then distribute three stun counters among any number of " +
        "tapped creatures your opponents control. (If a permanent with a stun counter would become " +
        "untapped, remove one from it instead.)"

    waterbendCost(isX = true)

    spell {
        target = TargetObject(
            optional = true,
            filter = TargetFilter.Creature,
            dynamicMaxCount = DynamicAmount.XValue,
        )
        effect = Effects.Composite(
            Effects.TapEachTarget(),
            Effects.DistributeCountersAmongFiltered(
                totalCounters = 3,
                counterType = Counters.STUN,
                filter = Filters.Creature.tapped().opponentControls(),
                minPerTarget = 0,
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Mitori"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9fd02eb4-1ef5-4a14-89a8-b25e720e8016.jpg?1764120208"
    }
}
