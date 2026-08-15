package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Orazca Frillback
 * {2}{G}
 * Creature — Dinosaur
 * 4/2
 *
 * Vanilla — no rules text.
 */
val OrazcaFrillback = card("Orazca Frillback") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 4
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Simon Dominic"
        flavorText = "The frillbacks of Orazca soak up the sun on their tall spinal fins. They look slow and sleepy—until they catch the scent of prey."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00c81160-192c-4077-8ed1-3643919a2025.jpg?1783935284"
    }
}
