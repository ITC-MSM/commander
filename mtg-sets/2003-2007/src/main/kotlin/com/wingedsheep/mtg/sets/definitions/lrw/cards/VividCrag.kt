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
 * Vivid Crag
 * Land
 * This land enters tapped with two charge counters on it.
 * {T}: Add {R}.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 *
 * One of the five Lorwyn vivid lands. Two self-scoped replacement effects model the entry line —
 * [EntersTapped] and [EntersWithCounters] apply to the same zone change — and the second mana
 * ability spends a charge counter as part of its cost, so once both are gone only the
 * mono-colored ability remains.
 */
val VividCrag = card("Vivid Crag") {
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "This land enters tapped with two charge counters on it.\n" +
        "{T}: Add {R}.\n" +
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
        effect = Effects.AddMana(Color.RED)
        description = "{T}: Add {R}."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE, 1))
        manaAbility = true
        effect = Effects.AddManaOfChoice()
        description = "{T}, Remove a charge counter from this land: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "275"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/539001dc-69f0-42c0-b5c3-37d7b12eb79e.jpg?1783942847"
    }
}
