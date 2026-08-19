package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Land Leeches
 * {1}{G}{G}
 * Creature — Leech
 * 2/2
 * First strike
 */
val LandLeeches = card("Land Leeches") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Leech"
    power = 2
    toughness = 2
    oracleText = "First strike"

    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Quinton Hoover"
        flavorText = "\"The standard cure for leeches requires the application of burning embers. Alternative methods must be devised should an ember of sufficient size prove more harmful than the leech.\" —Vervamon the Elder"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff99543d-86a1-44f8-88ec-aaec071d6c05.jpg?1783947931"
    }
}
