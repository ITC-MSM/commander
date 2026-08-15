package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Perilous Forays
 * {3}{G}{G}
 * Enchantment
 * {1}, Sacrifice a creature: Search your library for a land card with a basic land type, put it onto the battlefield tapped, then shuffle.
 */
val PerilousForays = card("Perilous Forays") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "{1}, Sacrifice a creature: Search your library for a land card with a basic land type, put it onto the battlefield tapped, then shuffle."
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Patterns.Library.searchLibrary(
            // "a land card with a basic land type" — any land whose type line includes a basic
            // land type (Plains/Island/Swamp/Mountain/Forest), e.g. the Ravnica shocklands, not
            // just basic lands. Mirrors Farseek's withAnySubtype rendering.
            filter = GameObjectFilter.Land.withAnySubtype("Plains", "Island", "Swamp", "Mountain", "Forest"),
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "176"
        artist = "Chris Dien"
        flavorText = "\"This is the place? This map has got to be wrong . . . .\"\n—Svania Trul, wayfinder novice, last words"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/4210a148-d16c-42de-b0d6-83c05c553dd4.jpg"
    }
}
