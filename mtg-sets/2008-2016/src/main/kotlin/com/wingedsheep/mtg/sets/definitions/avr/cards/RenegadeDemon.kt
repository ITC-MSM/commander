package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Renegade Demon
 * {3}{B}{B}
 * Creature — Demon
 * 5/3
 *
 * Vanilla — no rules text.
 */
val RenegadeDemon = card("Renegade Demon") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Tomasz Jedruszek"
        flavorText = "\"Have you ever cornered a wounded vampire? That's a walk in the cathedral garden in comparison.\"\n—Tristen, Cathar Marshal"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/395696f8-9be2-4925-852f-b783850e1ca2.jpg?1783940692"
    }
}
