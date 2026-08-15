package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trained Orgg
 * {6}{R}
 * Creature — Orgg
 * 6/6
 *
 * Vanilla — no rules text.
 */
val TrainedOrgg = card("Trained Orgg") {
    manaCost = "{6}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orgg"
    power = 6
    toughness = 6

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Eric Peterson"
        flavorText = "All orggs know how to kill; training teaches them *what* to kill."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/425540b0-c826-4814-b0df-032264b1c237.jpg?1783946025"
    }
}
