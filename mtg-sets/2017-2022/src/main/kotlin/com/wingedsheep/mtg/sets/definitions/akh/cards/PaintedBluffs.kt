package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Painted Bluffs
 *
 * Land — Desert
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 */
val PaintedBluffs = card("Painted Bluffs") {
    typeLine = "Land — Desert"
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddManaOfChoice()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Mark Poole"
        flavorText = "Centuries of scouring sands have carved and polished the rocky terrain of the Shefet."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b373131-2a1d-4710-8a11-c1b366a174d4.jpg?1783936445"
    }
}
