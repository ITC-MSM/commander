package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Jetmir's Garden
 * Land — Mountain Forest Plains
 * ({T}: Add {R}, {G}, or {W}.)
 * This land enters tapped.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val JetmirsGarden = card("Jetmir's Garden") {
    colorIdentity = "GRW"
    typeLine = "Land — Mountain Forest Plains"
    oracleText = "({T}: Add {R}, {G}, or {W}.)\nThis land enters tapped.\nCycling {3} ({3}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Kasia 'Kafis' Zielińska"
        flavorText = "The parklike Cabaretti grounds offer rest, food, and the perfect place to shake off a tail."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26d40e03-6de4-4373-9fbf-04c1dd79e995.jpg?1783923058"
    }
}
