package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jace's Ingenuity
 * {3}{U}{U}
 * Instant
 * Draw three cards.
 *
 * Canonical printing: Magic 2011, the card's earliest real-expansion printing. Reprinted in M15 as
 * a `Printing` row.
 */
val JacesIngenuity = card("Jace's Ingenuity") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw three cards."

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Igor Kieryluk"
        flavorText = "\"Brute force can sometimes kick down a locked door, but knowledge is a skeleton key.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b79bea30-3210-4944-b146-3624c9869f45.jpg?1783941824"
    }
}
