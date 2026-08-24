package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Geist of the Moors
 * {1}{W}{W}
 * Creature — Spirit
 * 3/1
 * Flying
 */
val GeistOfTheMoors = card("Geist of the Moors") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 1
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Aaron Miller"
        flavorText = "\"The battle is won. There's work to be done. / The Blessed Sleep must wait. / A fiend is about. It stalks the devout. / I'll save them from my fate.\"\n—\"The Good Geist's Vow\""
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90488074-730c-47a3-9a4b-9fec1da775ad.jpg?1783939202"
    }
}
