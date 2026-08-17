package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Spara's Headquarters
 * Land — Forest Plains Island
 * ({T}: Add {G}, {W}, or {U}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val SparasHeadquarters = card("Spara's Headquarters") {
    colorIdentity = "GUW"
    typeLine = "Land — Forest Plains Island"
    oracleText = "({T}: Add {G}, {W}, or {U}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Kieran Yanner"
        flavorText = "To most, the Nido Sanctuary is an office complex. To the Brokers, it's a vault of secrets."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/7363f1fb-9af3-4212-921f-d59533faf0e5.jpg?1783923052"
    }
}
