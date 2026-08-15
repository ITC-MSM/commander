package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blade of the Sixth Pride
 * {1}{W}
 * Creature — Cat Rebel
 * 3/1
 *
 * Vanilla — no rules text.
 */
val BladeOfTheSixthPride = card("Blade of the Sixth Pride") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Rebel"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Justin Sweet"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6ccfe28-ec08-4751-b65c-c40b2bafa955.jpg?1783943127"
    }
}
