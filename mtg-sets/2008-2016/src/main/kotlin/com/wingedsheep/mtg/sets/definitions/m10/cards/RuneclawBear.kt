package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Runeclaw Bear
 * {1}{G}
 * Creature — Bear
 * 2/2
 *
 * Vanilla — no rules text.
 */
val RuneclawBear = card("Runeclaw Bear") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "203"
        artist = "Jesper Ejsing"
        flavorText = "Bears aren't always as strong and as mean as you imagine. Some are even stronger and meaner."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/268bd9d5-4da1-4cbf-83f9-47f7aac1cfc3.jpg?1783942358"
    }
}
