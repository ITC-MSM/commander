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
 * Vivid Marsh
 * Land
 * This land enters tapped with two charge counters on it.
 * {T}: Add {B}.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 *
 * One of the five Lorwyn vivid lands. Two self-scoped replacement effects model the entry line —
 * [EntersTapped] and [EntersWithCounters] apply to the same zone change — and the second mana
 * ability spends a charge counter as part of its cost, so once both are gone only the
 * mono-colored ability remains.
 */
val VividMarsh = card("Vivid Marsh") {
    colorIdentity = "B"
    typeLine = "Land"
    oracleText = "This land enters tapped with two charge counters on it.\n" +
        "{T}: Add {B}.\n" +
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
        effect = Effects.AddMana(Color.BLACK)
        description = "{T}: Add {B}."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE, 1))
        manaAbility = true
        effect = Effects.AddManaOfChoice()
        description = "{T}, Remove a charge counter from this land: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "278"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31f806c7-0063-4270-b37c-5363c41a7621.jpg?1783942846"
    }
}
