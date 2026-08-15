package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wishcoin Crab
 * {3}{U}
 * Creature — Crab
 * 2/5
 *
 * Vanilla — no rules text.
 */
val WishcoinCrab = card("Wishcoin Crab") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "James Paick"
        flavorText = "\"What wishes do they grant? Mostly pinching-related ones.\"\n—Omik, superintendent of waterworks"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6580d6a2-ee21-4442-9842-18c65a172f49.jpg?1783934181"
    }
}
