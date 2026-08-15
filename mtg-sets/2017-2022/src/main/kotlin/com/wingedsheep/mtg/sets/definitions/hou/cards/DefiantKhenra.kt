package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Defiant Khenra
 * {1}{R}
 * Creature — Jackal Warrior
 * 2/2
 *
 * Vanilla — no rules text.
 */
val DefiantKhenra = card("Defiant Khenra") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Jackal Warrior"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "David Palumbo"
        flavorText = "There were those who saw the death of the gods and the city's collapse as a final test of worth. Some believed it meant the God-Pharaoh had been killed. Only a few realized they had been deceived."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67e3983d-b1ed-46a9-9ab0-96c4d0d77050.jpg?1783936030"
    }
}
