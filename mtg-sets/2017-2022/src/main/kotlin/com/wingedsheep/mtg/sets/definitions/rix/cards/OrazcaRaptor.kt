package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Orazca Raptor
 * {2}{R}{R}
 * Creature — Dinosaur
 * 3/4
 *
 * Vanilla — no rules text.
 */
val OrazcaRaptor = card("Orazca Raptor") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    power = 3
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Jakub Kasper"
        flavorText = "\"If you come across a raptor in the city, stay perfectly still. At least then you'll be well rested when you die.\"\n—Captain Brandis Thorn"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7080f86-0a9f-4471-a52b-0d44d19e6e59.jpg?1783935296"
    }
}
