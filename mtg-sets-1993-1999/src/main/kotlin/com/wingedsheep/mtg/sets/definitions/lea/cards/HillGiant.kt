package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hill Giant
 * {3}{R}
 * Creature — Giant
 * 3/3
 */
val HillGiant = card("Hill Giant") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0ddb98e8-13fe-4786-83f7-b72c56db135a.jpg?1559591379"
    }
}
