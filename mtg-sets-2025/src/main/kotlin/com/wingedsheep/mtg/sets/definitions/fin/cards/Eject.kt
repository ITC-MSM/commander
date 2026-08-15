package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Eject
 * {3}{U}
 * Instant
 * This spell can't be countered.
 * Return target nonland permanent to its owner's hand.
 * Draw a card.
 */
val Eject = card("Eject") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell can't be countered.\n" +
        "Return target nonland permanent to its owner's hand.\n" +
        "Draw a card."

    cantBeCountered = true

    spell {
        val t = target("target nonland permanent", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Composite(
            Effects.ReturnToHand(t),
            Effects.DrawCards(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "52"
        artist = "Ramza Psyru"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aec83c9a-8ec4-4a5a-b27f-0e74a2b3d21e.jpg?1748705947"
    }
}
