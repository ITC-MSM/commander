package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Scattered Groves
 *
 * Land — Forest Plains
 * ({T}: Add {G} or {W}.)
 * This land enters tapped.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ScatteredGroves = card("Scattered Groves") {
    colorIdentity = "GW"
    typeLine = "Land — Forest Plains"
    oracleText = "({T}: Add {G} or {W}.)\n" +
        "This land enters tapped.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Christine Choi"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/ccb541aa-3bf6-41f3-89e5-9c6c56ea210a.jpg?1783936445"
    }
}
