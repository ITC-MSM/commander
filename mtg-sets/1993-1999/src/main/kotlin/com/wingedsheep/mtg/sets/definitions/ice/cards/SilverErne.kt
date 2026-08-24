package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silver Erne
 * {3}{U}
 * Creature — Bird
 * 2/2
 *
 * Flying, trample
 *
 * Flying plus trample. Trample on a 1/3 flier is a printed oddity, not a transcription slip —
 * it is what the card says.
 */
val SilverErne = card("Silver Erne") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    power = 2
    toughness = 2
    oracleText = "Flying, trample"

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "Melissa A. Benson"
        flavorText = "\"I've seen a larger Erne knock a Giant to the ground and stay airborne. They move not with the wind, but as the wind.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/685076cc-098c-4f98-918c-0ad825eda10f.jpg"
    }
}
