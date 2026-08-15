package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Stalker
 * {5}{U}
 * Creature — Elemental
 * 4/5
 * Morph {4}{U}
 */
val GlacialStalker = card("Glacial Stalker") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 5
    oracleText = "Morph {4}{U}"

    morph = "{4}{U}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Daarken"
        flavorText = "\"Have you spent a winter high in the mountains, where the ice walks and speaks to the wind? It is not a place for those who have not learned respect.\" —Ulnok, Temur shaman"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/8821c7b9-702b-416f-b006-941ad57f9e11.jpg?1562789833"
    }
}
