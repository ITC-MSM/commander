package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talas Merchant
 * {1}{U}
 * Creature — Human Pirate
 * 1/3
 *
 * Vanilla — no rules text.
 */
val TalasMerchant = card("Talas Merchant") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Lubov"
        flavorText = "The trader let loose a laugh that made all around him check their purses."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f779d7e-6e37-49bc-b76d-3bb490ff142b.jpg?1783946482"
    }
}
