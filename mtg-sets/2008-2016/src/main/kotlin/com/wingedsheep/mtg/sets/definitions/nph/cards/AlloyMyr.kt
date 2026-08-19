package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alloy Myr
 * {3}
 * Artifact Creature — Myr
 * 2/2
 *
 * {T}: Add one mana of any color.
 */
val AlloyMyr = card("Alloy Myr") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Myr"
    oracleText = "{T}: Add one mana of any color."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Matt Cavotta"
        flavorText = "With or without witnesses, the suns continued their prismatic dance."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abd3350b-89fb-40b4-a942-28e0c8c274aa.jpg?1783941297"
    }
}
