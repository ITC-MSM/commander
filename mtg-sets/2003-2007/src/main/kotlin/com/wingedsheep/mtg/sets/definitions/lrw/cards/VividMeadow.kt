package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Vivid Meadow
 * Land
 * This land enters tapped with two charge counters on it.
 * {T}: Add {W}.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 *
 * One of the five Lorwyn vivid lands. Two self-scoped replacement effects model the entry line —
 * [EntersTapped] and [EntersWithCounters] apply to the same zone change — and the second mana
 * ability spends a charge counter as part of its cost, so once both are gone only the
 * mono-colored ability remains.
 */
val VividMeadow = card("Vivid Meadow") {
    colorIdentity = "W"
    typeLine = "Land"
    oracleText = "This land enters tapped with two charge counters on it.\n" +
        "{T}: Add {W}.\n" +
        "{T}, Remove a charge counter from this land: Add one mana of any color."

    replacementEffect(EntersTapped())
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 2,
            selfOnly = true,
        )
    )

    activatedAbility {
        cost = Costs.Tap
        manaAbility = true
        effect = Effects.AddMana(Color.WHITE)
        description = "{T}: Add {W}."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE, 1))
        manaAbility = true
        effect = Effects.AddManaOfChoice()
        description = "{T}, Remove a charge counter from this land: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "279"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a535790b-d416-4807-bca0-acf6b192101c.jpg?1783942846"
    }
}
