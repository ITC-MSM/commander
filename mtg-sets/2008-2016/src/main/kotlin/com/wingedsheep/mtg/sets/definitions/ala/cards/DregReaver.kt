package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dreg Reaver
 * {4}{B}
 * Creature — Zombie Beast
 * 4/3
 *
 * Vanilla — no rules text.
 */
val DregReaver = card("Dreg Reaver") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Beast"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Thomas M. Baxa"
        flavorText = "\"On our thirty-fourth day of digging, we unearthed a chamber that contained the intact remains of several species long extinct from Grixis. One in particular should make a fine siege engine . . . .\"\n—Last notes of Shungus Nod, fleshcrafter"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7771eba-bc2d-40f2-bab4-5e9cc4fe8f34.jpg?1783942567"
    }
}
