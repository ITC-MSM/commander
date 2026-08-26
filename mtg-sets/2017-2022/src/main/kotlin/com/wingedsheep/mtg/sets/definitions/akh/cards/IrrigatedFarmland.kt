package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Irrigated Farmland
 *
 * Land — Plains Island
 * ({T}: Add {W} or {U}.)
 * This land enters tapped.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val IrrigatedFarmland = card("Irrigated Farmland") {
    colorIdentity = "UW"
    typeLine = "Land — Plains Island"
    oracleText = "({T}: Add {W} or {U}.)\n" +
        "This land enters tapped.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "245"
        artist = "Jonas De Ro"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d87fcc0-cef5-4410-adf1-91c5f50c1d01.jpg?1783936447"
    }
}
