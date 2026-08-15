package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barbarian Horde
 * {3}{R}
 * Creature — Human Barbarian Soldier
 * 3/3
 *
 * Vanilla — no rules text.
 */
val BarbarianHorde = card("Barbarian Horde") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Barbarian Soldier"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Kuang Sheng"
        flavorText = "Only twenty years after the Sima clan united the empire, invading barbarians divided it again."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1f930c2-e828-4566-b2df-3b054f311be5.jpg?1783946109"
    }
}
