package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Guess
 * {R}{R}
 * Sorcery
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards.
 *
 * Canonical printing: Magic 2013, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 */
val WildGuess = card("Wild Guess") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard a card.\n" +
            "Draw two cards."

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Lucas Graciano"
        flavorText = "No guts, no glory."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4e513b8-25c2-4645-abcc-a6e9d5f51e09.jpg"
    }
}
