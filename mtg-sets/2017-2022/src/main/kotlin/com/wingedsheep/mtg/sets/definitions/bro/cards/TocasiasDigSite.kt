package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tocasia's Dig Site
 * Land
 * {T}: Add {C}.
 * {3}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val TocasiasDigSite = card("Tocasia's Dig Site") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{3}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Nadia Hurianova"
        flavorText = "Sent by their noble parents to toughen up and learn something of the world's past, Urza and Mishra instead uncovered Terisiare's future."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23d4b90c-95b1-4828-bc08-7067da0d5364.jpg?1783920004"
    }
}
