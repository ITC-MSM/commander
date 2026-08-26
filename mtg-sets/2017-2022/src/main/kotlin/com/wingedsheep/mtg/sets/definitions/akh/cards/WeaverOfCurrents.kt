package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Weaver of Currents
 * {1}{G}{U}
 * Creature — Snake Druid
 * 2/2
 * {T}: Add {C}{C}.
 */
val WeaverOfCurrents = card("Weaver of Currents") {
    manaCost = "{1}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Snake Druid"
    oracleText = "{T}: Add {C}{C}."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Winona Nelson"
        flavorText = "\"Your waters sustain the living and carry the dead. Mighty Luxa, let your power flow through me!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dac35181-baae-4c50-b397-a10b234833e5.jpg?1783936459"
    }
}
