package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val YourFateEndsHere = card("Your Fate Ends Here") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target creature or planeswalker with mana value 3 or greater. Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"

    spell {
        val permanent = target("target",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker).manaValueAtLeast(3)))
        effect = Effects.Composite(Effects.Destroy(permanent), Effects.Surveil(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Andrew Mar"
        flavorText = "\"The future looms large over the present, and you're blocking my view.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25000a17-b701-4d69-b2ef-2c74029199d3.jpg?1789729751"
        inBooster = false
    }
}
