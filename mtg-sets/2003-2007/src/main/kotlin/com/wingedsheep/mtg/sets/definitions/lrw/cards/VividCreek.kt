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
 * Vivid Creek
 * Land
 * This land enters tapped with two charge counters on it.
 * {T}: Add {U}.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 *
 * One of the five Lorwyn vivid lands. Two self-scoped replacement effects model the entry line —
 * [EntersTapped] and [EntersWithCounters] apply to the same zone change — and the second mana
 * ability spends a charge counter as part of its cost, so once both are gone only the
 * mono-colored ability remains.
 */
val VividCreek = card("Vivid Creek") {
    colorIdentity = "U"
    typeLine = "Land"
    oracleText = "This land enters tapped with two charge counters on it.\n" +
        "{T}: Add {U}.\n" +
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
        effect = Effects.AddMana(Color.BLUE)
        description = "{T}: Add {U}."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE, 1))
        manaAbility = true
        effect = Effects.AddManaOfChoice()
        description = "{T}, Remove a charge counter from this land: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "276"
        artist = "Fred Fields"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e3c2935-84ee-446d-b247-2f574ea84a8f.jpg?1783942846"
    }
}
