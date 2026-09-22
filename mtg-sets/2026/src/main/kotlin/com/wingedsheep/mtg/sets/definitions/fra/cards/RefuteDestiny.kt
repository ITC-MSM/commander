package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val RefuteDestiny = card("Refute Destiny") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target creature or planeswalker that's green or blue. Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"

    spell {
        val permanent = target("green or blue creature or planeswalker",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker)
                .withAnyColor(Color.GREEN, Color.BLUE)))
        effect = Effects.Composite(Effects.Exile(permanent), Effects.Surveil(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Billy Christian"
        flavorText = "Quandrix believed in finding the equations of the natural world. Hexhaven disrupted the pattern of existence."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c588954-c6eb-4aae-a2fa-0651ccf2d90a.jpg?1789470778"
        inBooster = false
    }
}
