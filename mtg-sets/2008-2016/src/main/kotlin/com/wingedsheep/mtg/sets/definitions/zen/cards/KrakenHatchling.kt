package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kraken Hatchling
 * {U}
 * Creature — Kraken
 * 0/4
 *
 * Vanilla — no rules text.
 */
val KrakenHatchling = card("Kraken Hatchling") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Kraken"
    power = 0
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Jason Felix"
        flavorText = "A spike and a maul are needed to crack their shells, but the taste is worth the effort."
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45d100a3-93f2-428c-8f54-8807e71f2638.jpg?1783942164"
    }
}
