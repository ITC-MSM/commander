package com.wingedsheep.mtg.sets.definitions.hml.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dwarven Trader
 * {R}
 * Creature — Dwarf
 * 1/1
 *
 * Vanilla — no rules text.
 */
val DwarvenTrader = card("Dwarven Trader") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72a"
        artist = "Margaret Organ-Kean"
        flavorText = "\"Their definition of 'fair profit' is certainly novel.\"\n—Reveka, Wizard Savant"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4db9aa47-f42b-41e9-948c-8b012c3809fb.jpg?1783947283"
    }
}
