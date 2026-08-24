package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sky Spirit
 * {1}{W}{U}
 * Creature — Spirit
 * 2/2
 * Flying, first strike
 */
val SkySpirit = card("Sky Spirit") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 2
    oracleText = "Flying, first strike"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "271"
        artist = "Rebecca Guay"
        flavorText = "\"Like a strain of music: easy to remember but impossible to catch.\"\n" +
            "—Mirri of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb8efbec-e8bf-4e34-bf13-b43916d2e9ff.jpg"
    }
}
