package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stone-Throwing Devils
 * {B}
 * Creature — Devil
 * 1/1
 * First strike
 */
val StoneThrowingDevils = card("Stone-Throwing Devils") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Devil"
    power = 1
    toughness = 1
    oracleText = "First strike"
    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Ken Meyer, Jr."
        flavorText = "Sometimes those with the most sin cast the first stones."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1c387dd-1347-4443-91ce-b71f7ccdceba.jpg?1740685920"
    }
}
