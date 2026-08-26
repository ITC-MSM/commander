package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nimble-Blade Khenra
 * {1}{R}
 * Creature — Jackal Warrior
 * 1/3
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val NimbleBladeKhenra = card("Nimble-Blade Khenra") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Jackal Warrior"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"
    power = 1
    toughness = 3

    prowess()

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Tomasz Jedruszek"
        flavorText = "\"In the Hour of Glory, the gods and the untested will prove their worth before the God-Pharaoh.\"\n—*The Accounting of Hours*"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e36a17b-89d6-4de9-867b-9762afedb4f1.jpg?1783936484"
    }
}
