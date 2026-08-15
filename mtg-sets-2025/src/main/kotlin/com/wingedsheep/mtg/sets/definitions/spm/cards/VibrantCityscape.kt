package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Vibrant Cityscape
 * Land
 * {T}, Sacrifice this land: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.
 */
val VibrantCityscape = card("Vibrant Cityscape") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}, Sacrifice this land: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Wei Guan"
        flavorText = "If you can make it here, you can make it anywhere."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c110fa1-2320-4652-b282-ed064a9ec9a9.jpg?1757378220"
    }
}
