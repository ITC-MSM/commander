package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Maned Serval
 * {1}{W}
 * Creature — Cat
 * 1/4
 * Vigilance
 */
val ManedServal = card("Maned Serval") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 4
    oracleText = "Vigilance"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Jonathan Kuo"
        flavorText = "\"Don't search the ruins of Orn during the day. Nethroi's many eyes will spot you instantly. Don't go at night either. You'll be dinner for the servals. Maybe just don't go.\"\n—Rasai, Indatha hunter"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ac51e35-e2c5-4457-981c-e59894584288.jpg"
    }
}
