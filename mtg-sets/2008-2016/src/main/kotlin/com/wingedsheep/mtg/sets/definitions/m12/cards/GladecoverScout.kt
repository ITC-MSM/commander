package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gladecover Scout
 * {G}
 * Creature — Elf Scout
 * 1 / 1
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 *
 * Canonical printing: Magic 2012, the card's earliest real printing. Reprinted in Magic 2014.
 */
val GladecoverScout = card("Gladecover Scout") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout"
    power = 1
    toughness = 1
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Allen Williams"
        flavorText = "\"The forest is my cover and I hold it close. In such a tight embrace, there is no room for wickedness.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26710d5c-01d1-498b-9f54-521dfd195843.jpg"
    }
}
