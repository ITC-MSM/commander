package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Magmatic Insight
 * {R}
 * Sorcery
 *
 * As an additional cost to cast this spell, discard a land card.
 * Draw two cards.
 */
val MagmaticInsight = card("Magmatic Insight") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard a land card.\n" +
        "Draw two cards."

    additionalCost(Costs.additional.DiscardCards(1, Filters.Land))

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Ryan Barger"
        flavorText = "Chief among the tenets of Purphoros is that one must destroy in order to create."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f00192e0-439d-43b2-882c-90a2d52103f8.jpg"
    }
}
