package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nether Horror
 * {3}{B}
 * Creature — Horror
 * 4/2
 *
 * Vanilla — no rules text.
 */
val NetherHorror = card("Nether Horror") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 4
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Allen Williams"
        flavorText = "\"My dreams darkened and seeped into my waking hours. Spellcraft and nightmare merged to create this monstrosity.\"\n—Sunniva, witch of Holm Hollow"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c217b672-c724-4fc2-936c-b3f0feaf6ea0.jpg?1783941813"
    }
}
