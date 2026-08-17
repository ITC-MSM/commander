package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Raffine's Tower
 * Land — Plains Island Swamp
 * ({T}: Add {W}, {U}, or {B}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val RaffinesTower = card("Raffine's Tower") {
    colorIdentity = "BUW"
    typeLine = "Land — Plains Island Swamp"
    oracleText = "({T}: Add {W}, {U}, or {B}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "254"
        artist = "Sam White"
        flavorText = "The Obscura's Cloud Spire dominates the skyline, its eye a beacon of progress that sees all."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2c56479-4bee-4edb-80d7-4af010b7c793.jpg?1783923055"
    }
}
