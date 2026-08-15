package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sheltered Thicket
 *
 * Land — Mountain Forest
 * ({T}: Add {R} or {G}.)
 * This land enters tapped.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ShelteredThicket = card("Sheltered Thicket") {
    colorIdentity = "RG"
    typeLine = "Land — Mountain Forest"
    oracleText = "({T}: Add {R} or {G}.)\nThis land enters tapped.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "248"
        artist = "Sung Choi"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d23cd764-3f5c-4c93-ba83-e5ff397003a2.jpg?1543676414"
    }
}
