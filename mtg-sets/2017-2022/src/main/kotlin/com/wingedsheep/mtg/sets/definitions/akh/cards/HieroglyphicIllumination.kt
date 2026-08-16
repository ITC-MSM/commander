package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Hieroglyphic Illumination
 * {3}{U}
 * Instant
 * Draw two cards.
 * Cycling {U} ({U}, Discard this card: Draw a card.)
 */
val HieroglyphicIllumination = card("Hieroglyphic Illumination") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw two cards.\nCycling {U} ({U}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.DrawCards(2)
    }

    keywordAbility(KeywordAbility.cycling("{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Raoul Vitale"
        flavorText = "\"The answers are here. This is the way through the trial.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c60a0e75-53bb-43e4-890f-0e1972a7e0b9.jpg?1783936520"
    }
}
