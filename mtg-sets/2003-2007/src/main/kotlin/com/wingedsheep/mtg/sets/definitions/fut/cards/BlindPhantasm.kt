package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blind Phantasm
 * {2}{U}
 * Creature — Illusion
 * 2/3
 *
 * Vanilla — no rules text.
 */
val BlindPhantasm = card("Blind Phantasm") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Khang Le"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b26ee7b-de1a-4a39-9580-89941c3d0f21.jpg?1783943118"
    }
}
