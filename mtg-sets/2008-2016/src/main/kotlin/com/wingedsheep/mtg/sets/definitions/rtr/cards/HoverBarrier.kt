package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hover Barrier
 * {2}{U}
 * Creature — Illusion Wall
 * 0/6
 *
 * Defender, flying
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val HoverBarrier = card("Hover Barrier") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion Wall"
    oracleText = "Defender, flying"
    power = 0
    toughness = 6

    keywords(Keyword.DEFENDER, Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Mathias Kollros"
        flavorText = "As whispers and rumors increased, so did the demand for fail-safe barriers."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/884afdb3-0d5f-45a1-b57e-6c3760aa0031.jpg?1783940369"
    }
}
