package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Whirl Turtle
 * {2}{U}
 * Creature — Turtle
 * 0/5
 *
 * Vanilla — no rules text.
 */
val ArmoredWhirlTurtle = card("Armored Whirl Turtle") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Turtle"
    power = 0
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Tingting Yeh"
        flavorText = "Not all enormous beasts are quick to anger."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcc87fdf-6473-4b91-a8b9-2986e57dc071.jpg?1783934633"
    }
}
