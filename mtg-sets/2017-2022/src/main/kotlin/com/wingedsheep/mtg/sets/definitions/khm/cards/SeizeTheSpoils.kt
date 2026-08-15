package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seize the Spoils
 * {2}{R}
 * Sorcery
 *
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards and create a Treasure token. (It's an artifact with
 * "{T}, Sacrifice this token: Add one mana of any color.")
 *
 * Canonical printing: Kaldheim (KHM) — the earliest real expansion printing.
 * Later reprints (SOS, FDN, J25) contribute only a `Printing` row.
 */
val SeizeTheSpoils = card("Seize the Spoils") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard a card.\n" +
        "Draw two cards and create a Treasure token. (It's an artifact with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")"

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.DrawCards(2).then(Effects.CreateTreasure(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Jesper Ejsing"
        flavorText = "\"Grab what you want and burn the rest. Leave nothing behind.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3b7a69c-75d2-49a6-ab56-ef608d0b0208.jpg?1631049630"
    }
}
