package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wardscale Crocodile
 * {4}{G}
 * Creature — Crocodile
 * 5/3
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val WardscaleCrocodile = card("Wardscale Crocodile") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Crocodile"
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 5
    toughness = 3

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Zezhou Chen"
        flavorText = "\"The Eternals had to endure Amonkhet's five trials. Let's see if they can pass the Trial of Ravnica.\"\n—Jace Beleren"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa5341b9-06e4-4360-a75d-f405d468276e.jpg"
    }
}
