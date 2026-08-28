package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alaborn Grenadier
 * {W}{W}
 * Creature — Human Soldier
 * 2/2
 * Vigilance
 */
val AlabornGrenadier = card("Alaborn Grenadier") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Vigilance"
    power = 2
    toughness = 2
    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "David A. Cherry"
        flavorText = "\"Ever proud, ever vigilant.\"\n—Grenadier motto"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/153b7197-57a7-4e38-bd4a-4550b9d22dd8.jpg"
    }
}
