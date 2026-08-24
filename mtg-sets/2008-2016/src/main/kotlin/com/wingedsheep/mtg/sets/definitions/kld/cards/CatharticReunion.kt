package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cathartic Reunion
 * {1}{R}
 * Sorcery
 * As an additional cost to cast this spell, discard two cards.
 * Draw three cards.
 *
 * The discard is a real additional *cost* — paid on announcement, before the spell resolves — so it
 * is [Costs.additional].DiscardCards rather than a first step of the spell effect (Tormenting
 * Voice). That ordering is why you can't draw into the cards you discard.
 */
val CatharticReunion = card("Cathartic Reunion") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard two cards.\n" +
        "Draw three cards."

    additionalCost(Costs.additional.DiscardCards(2))

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Howard Lyon"
        flavorText = "The chasm of years and worlds collapsed under the power of their embrace."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c68a6226-6dd7-4e1a-9e8a-124eef2caa13.jpg?1783937197"
    }
}
