package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moor Fiend
 * {3}{B}
 * Creature — Horror
 * 3/3
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 *
 * Swampwalk alone. `BlockEvasionRules` maps `Keyword.SWAMPWALK` to `Subtype.SWAMP`, so the
 * land-type check is the keyword's own rule and needs no static ability here.
 */
val MoorFiend = card("Moor Fiend") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 3
    toughness = 3
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Anson Maddocks"
        flavorText = "\"Let them close the gates of Krov from dusk until dawn if they so choose. It matters not. My fiends shall yet rend their flesh from their bones.\"\n—Lim-Dûl, the Necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57089dd4-e30d-498d-9341-43c104c6f3f9.jpg"
    }
}
