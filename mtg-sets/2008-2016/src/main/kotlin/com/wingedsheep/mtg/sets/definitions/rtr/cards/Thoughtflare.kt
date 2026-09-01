package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thoughtflare
 * {3}{U}{R}
 * Instant
 *
 * Draw four cards, then discard two cards.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * "Draw, then discard" is sequenced by the composite, which matters: the two discarded cards may
 * be among the four just drawn.
 */
val Thoughtflare = card("Thoughtflare") {
    manaCost = "{3}{U}{R}"
    colorIdentity = "RU"
    typeLine = "Instant"
    oracleText = "Draw four cards, then discard two cards."

    spell {
        effect = Effects.Composite(
            Effects.DrawCards(4),
            Patterns.Hand.discardCards(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "David Rapoza"
        flavorText = "\"If this is thinking, I don't know what I was doing before.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d90514aa-e356-4502-9e0e-76ab7644a07a.jpg?1783940331"
    }
}
