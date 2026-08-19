package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Signpost Scarecrow
 * {4}
 * Artifact Creature — Scarecrow
 * 2/4
 *
 * Vigilance
 * {2}: Add one mana of any color.
 */
val SignpostScarecrow = card("Signpost Scarecrow") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    oracleText = "Vigilance\n{2}: Add one mana of any color."
    power = 2
    toughness = 4

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "231"
        artist = "Jung Park"
        flavorText = "\"Accursed scarecrow! Sending folk in every direction is the same as sending them nowhere at all.\"\n—Corliss the Wanderer"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2c5f336-c100-4bec-89d5-548f60064d7f.jpg?1783932583"
    }
}
