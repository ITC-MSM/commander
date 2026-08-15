package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Utopia Tree
 * {1}{G}
 * Creature — Plant
 * 0/2
 * {T}: Add one mana of any color.
 */
val UtopiaTree = card("Utopia Tree") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    oracleText = "{T}: Add one mana of any color."
    power = 0
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "219"
        artist = "Tony Szczudlo"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/720452e9-3245-4b0e-94b6-843cbcb641a5.jpg?1562917779"
    }
}
