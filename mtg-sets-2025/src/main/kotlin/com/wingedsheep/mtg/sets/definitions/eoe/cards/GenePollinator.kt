package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gene Pollinator
 * {G}
 * Artifact Creature — Robot Insect
 * 1/2
 * {T}, Tap an untapped permanent you control: Add one mana of any color.
 */
val GenePollinator = card("Gene Pollinator") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Robot Insect"
    power = 1
    toughness = 2
    oracleText = "{T}, Tap an untapped permanent you control: Add one mana of any color."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.TapAnotherPermanent()
        )
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Milivoj Ćeran"
        flavorText = "Eumidians have engineered a device or species to manage every stage of terrasymbiosis."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce7a8eec-a029-4ee1-b2d6-405d903d4640.jpg?1752947315"
    }
}
