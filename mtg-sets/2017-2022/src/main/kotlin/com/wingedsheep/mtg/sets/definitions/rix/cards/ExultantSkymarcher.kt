package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Exultant Skymarcher
 * {1}{W}{W}
 * Creature — Vampire Soldier
 * 2/3
 * Flying
 */
val ExultantSkymarcher = card("Exultant Skymarcher") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "Flying"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Ryan Yee"
        flavorText = "\"We have come at last to this holiest of holy places. What was stolen from us long ago is ours once again.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/f/fffe7b2b-22c3-4e6a-9b1b-c6d7b29b9f86.jpg?1783935339"
    }
}
