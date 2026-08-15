package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alaborn Trooper
 * {2}{W}
 * Creature — Human Soldier
 * 2/3
 *
 * Vanilla — no rules text.
 */
val AlabornTrooper = card("Alaborn Trooper") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Lubov"
        flavorText = "\"I dedicate my body to my country And my life to my King.\"\n—Alaborn Soldier's Oath"
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1cd30b4-4ed8-467e-808e-b0caf4196d90.jpg?1783946495"
    }
}
