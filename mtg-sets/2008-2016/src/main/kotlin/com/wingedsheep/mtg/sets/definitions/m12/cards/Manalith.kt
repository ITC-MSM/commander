package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Manalith
 * {3}
 * Artifact
 * {T}: Add one mana of any color.
 *
 * A plain mana rock. [Effects.AddAnyColorMana] with its default amount of one is the whole
 * ability; `manaAbility = true` is CR 605.1a, and the builder derives the `ManaAbility` timing
 * from it.
 *
 * M12 is Manalith's earliest printing, so the canonical definition lives here; later sets
 * (M19 among them) carry `Printing` rows.
 */
val Manalith = card("Manalith") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        description = "{T}: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "212"
        artist = "Charles Urbach"
        flavorText = "Planeswalkers seek out great monuments throughout the Multiverse, knowing that their builders were unknowingly drawn by the convergence of mana in the area."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17bf5f25-82b4-460c-94da-b84daa8a53d9.jpg"
    }
}
