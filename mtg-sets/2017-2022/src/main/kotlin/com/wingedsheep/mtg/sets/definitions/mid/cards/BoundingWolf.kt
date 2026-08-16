package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bounding Wolf
 * {2}{G}
 * Creature — Wolf
 * 3/2
 * Flash
 * Reach
 */
val BoundingWolf = card("Bounding Wolf") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 2
    oracleText = "Flash\nReach"

    keywords(Keyword.FLASH, Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Andrea Radeck"
        flavorText = "With their usual prey scared off by werewolves, the wolves of the Ulvenwald adopted inventive new hunting techniques."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74e22ed6-9d39-4feb-8c64-64cdd8313816.jpg?1783925585"
    }
}
