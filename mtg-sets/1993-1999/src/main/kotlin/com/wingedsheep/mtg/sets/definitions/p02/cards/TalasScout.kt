package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talas Scout
 * {1}{U}
 * Creature — Human Pirate Scout
 * 1/2
 * Flying
 */
val TalasScout = card("Talas Scout") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate Scout"
    oracleText = "Flying"
    power = 1
    toughness = 2
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Heather Hudson"
        flavorText = "\"Scouting a battle before you fight it is just good business.\"\n—Jefan, Talas ship captain"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48e12f17-855e-47e0-b7e3-df5c388b01bb.jpg"
    }
}
