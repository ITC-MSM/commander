package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eldrazi Devastator
 * {8}
 * Creature — Eldrazi
 * 8/9
 * Trample
 */
val EldraziDevastator = card("Eldrazi Devastator") {
    manaCost = "{8}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 8
    toughness = 9
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Joseph Meehan"
        flavorText = "\"No matter how big your champion, theirs is bigger. No matter how great your numbers, theirs " +
            "are greater. No matter how voracious your appetite, they are hungrier. That is why the " +
            "Eldrazi will win.\"\n" +
            "—Kalitas, thrall of Ulamog"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04b13e32-01b9-4a86-a3df-ca8b784c6a6c.jpg?1783938225"
    }
}
