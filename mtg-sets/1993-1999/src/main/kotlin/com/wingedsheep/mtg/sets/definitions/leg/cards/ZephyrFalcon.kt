package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zephyr Falcon
 * {1}{U}
 * Creature — Bird
 * 1/1
 *
 * Flying, vigilance
 */
val ZephyrFalcon = card("Zephyr Falcon") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 1
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Heather Hudson"
        flavorText = "Although greatly prized among falconers, the Zephyr Falcon is capricious and not easily " +
            "tamed."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25a173fd-e10c-45f8-a6e5-ad7a747a8050.jpg?1783948069"
    }
}
