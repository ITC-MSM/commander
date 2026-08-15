package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dune Beetle
 * {1}{B}
 * Creature — Insect
 * 1/4
 *
 * Vanilla — no rules text.
 */
val DuneBeetle = card("Dune Beetle") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Grzegorz Rutkowski"
        flavorText = "The scouring sands of Shefet polish its carapace, and the ranks of the cursed fill its belly."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/923cb904-c725-4d57-bc17-7aa87a7cd8e0.jpg?1783936506"
    }
}
