package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shrike Force
 * {2}{W}
 * Creature — Bird Knight
 * 1/3
 * Flying, double strike, vigilance
 */
val ShrikeForce = card("Shrike Force") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Knight"
    oracleText = "Flying, double strike, vigilance"
    power = 1
    toughness = 3

    keywords(Keyword.FLYING, Keyword.DOUBLE_STRIKE, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Néstor Ossandón Leal"
        flavorText = "\"I've trained every one of them in aerial maneuvers, talon strikes, and dive precision. They are the top of my class, and they may surpass me one day.\"\n—Quickwing, master flyer"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/306fec2c-d8b7-4f4b-8f58-10e3b9f3158f.jpg?1721425946"
    }
}
