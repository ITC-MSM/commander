package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Staunch Shieldmate
 * {W}
 * Creature — Dwarf Soldier
 * 1/3
 *
 * Vanilla — no rules text.
 */
val StaunchShieldmate = card("Staunch Shieldmate") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Soldier"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Bartłomiej Gaweł"
        flavorText = "\"Gilded with gold from a dragon's trove, it is! And etched with the image of the dragon I slew to take it.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db17f25a-32d1-469b-bb5f-f1761e227990.jpg?1783930733"
    }
}
