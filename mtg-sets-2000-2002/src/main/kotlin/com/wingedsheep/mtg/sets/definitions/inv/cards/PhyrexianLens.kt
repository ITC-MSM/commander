package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Phyrexian Lens
 * {3}
 * Artifact
 * {T}, Pay 1 life: Add one mana of any color.
 */
val PhyrexianLens = card("Phyrexian Lens") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "{T}, Pay 1 life: Add one mana of any color."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "307"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ec9a91d-7af0-44a8-839f-fb9960be0ddd.jpg?1562917154"
    }
}
