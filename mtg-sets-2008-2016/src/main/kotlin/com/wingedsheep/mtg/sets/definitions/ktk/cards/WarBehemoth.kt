package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * War Behemoth
 * {5}{W}
 * Creature — Beast
 * 3/6
 * Morph {4}{W}
 */
val WarBehemoth = card("War Behemoth") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 6
    oracleText = "Morph {4}{W}"

    morph = "{4}{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Zoltan Boros"
        flavorText = "\"The Houses always hope for peace, but we always pack for war.\" — Gvar Barzeel, krumar commander"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/652109b9-d607-42b6-945d-0c0dd5bba89c.jpg?1562787724"
    }
}
