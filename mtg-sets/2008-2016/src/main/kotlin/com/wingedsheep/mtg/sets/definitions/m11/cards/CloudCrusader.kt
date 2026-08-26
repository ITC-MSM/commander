package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloud Crusader
 * {2}{W}{W}
 * Creature — Human Knight
 * 2/3
 *
 * Flying
 * First strike (This creature deals combat damage before creatures without first strike.)
 */
val CloudCrusader = card("Cloud Crusader") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "First strike (This creature deals combat damage before creatures without first strike.)"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Aleksi Briclot"
        flavorText = "Each crusader bonds with a griffin fledgling while still a child, and the two grow up as siblings."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83ce09da-6c1d-46ac-870e-ff58ceaba116.jpg?1783941836"
    }
}
