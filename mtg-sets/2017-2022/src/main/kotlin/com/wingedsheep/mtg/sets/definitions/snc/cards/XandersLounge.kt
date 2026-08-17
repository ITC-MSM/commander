package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Xander's Lounge
 * Land — Island Swamp Mountain
 * ({T}: Add {U}, {B}, or {R}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val XandersLounge = card("Xander's Lounge") {
    colorIdentity = "BRU"
    typeLine = "Land — Island Swamp Mountain"
    oracleText = "({T}: Add {U}, {B}, or {R}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "James Paick"
        flavorText = "Maestros agents can lie low in high style at the opulent Shadow Hostel."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54f449ff-4025-465e-9ec5-a5cf42c4c9d3.jpg?1783923052"
    }
}
