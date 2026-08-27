package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sacred Wolf
 * {2}{G}
 * Creature — Wolf
 * 3/1
 *
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val SacredWolf = card("Sacred Wolf") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 1
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Matt Stewart"
        flavorText = "\"I raised my bow, and the wolf stared at me. Under its gaze, my finger would not release the string.\"\n" +
            "—Aref the Hunter"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2bffe20-c469-4ac8-a8a9-361a244f4cfe.jpg?1783941792"
    }
}
