package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Queen's Bay Soldier
 * {1}{B}
 * Creature — Vampire Soldier
 * 2/2
 *
 * Vanilla — no rules text.
 */
val QueensBaySoldier = card("Queen's Bay Soldier") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Soldier"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Jesper Ejsing"
        flavorText = "The soldiers of the Legion of Dusk have come to the colonies at Queen's Bay in search of glory and riches. They are veterans of centuries of warfare, and they thirst for conquest."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce2ca2e6-f920-4529-88d2-d984bdb7490a.jpg?1783935757"
    }
}
