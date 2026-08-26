package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Fetid Pools
 *
 * Land — Island Swamp
 * ({T}: Add {U} or {B}.)
 * This land enters tapped.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val FetidPools = card("Fetid Pools") {
    colorIdentity = "BU"
    typeLine = "Land — Island Swamp"
    oracleText = "({T}: Add {U} or {B}.)\n" +
        "This land enters tapped.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "243"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae703d94-6f1f-463b-ab25-1b3f462e7e78.jpg?1783936446"
    }
}
