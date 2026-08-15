package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wandering Tombshell
 * {3}{B}
 * Creature — Zombie Turtle
 * 1/6
 *
 * Vanilla — no rules text.
 */
val WanderingTombshell = card("Wandering Tombshell") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Turtle"
    power = 1
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Yeong-Hao Han"
        flavorText = "The crumbling temples on the tortoise's back are monuments to the decadence of the ancient Sultai. Though it harkens back to the era of the khans, Silumgar allows it to walk his territory as a warning to those who would oppose him."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e66e1d97-d676-471a-a140-deb39600a7a9.jpg?1783938592"
    }
}
