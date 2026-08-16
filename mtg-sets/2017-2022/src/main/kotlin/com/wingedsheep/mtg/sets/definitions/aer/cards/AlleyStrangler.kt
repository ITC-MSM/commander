package com.wingedsheep.mtg.sets.definitions.aer.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alley Strangler
 * {2}{B}
 * Creature — Aetherborn Rogue
 * 2/3
 * Menace
 */
val AlleyStrangler = card("Alley Strangler") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Rogue"
    power = 2
    toughness = 3
    oracleText = "Menace"

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Enora Mercier"
        flavorText = "\"You never know what day might be your last.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a131d558-5f6b-448b-a378-1882e2d02bd2.jpg?1783936767"
    }
}
