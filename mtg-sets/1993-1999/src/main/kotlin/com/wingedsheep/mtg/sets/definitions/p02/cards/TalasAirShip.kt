package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talas Air Ship
 * {3}{U}
 * Creature — Human Pirate
 * 3/2
 * Flying
 */
val TalasAirShip = card("Talas Air Ship") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    oracleText = "Flying"
    power = 3
    toughness = 2
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Mark Tedin"
        flavorText = "\"Their ships pollute our air as they themselves pollute our forest.\"\n—Arathel, elvish queen"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80bc3159-f585-45cd-8578-f3bf2fa9b2d1.jpg"
    }
}
