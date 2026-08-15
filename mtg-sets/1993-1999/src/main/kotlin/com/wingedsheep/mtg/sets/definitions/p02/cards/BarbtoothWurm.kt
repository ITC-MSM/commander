package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barbtooth Wurm
 * {5}{G}
 * Creature — Wurm
 * 6/4
 *
 * Vanilla — no rules text.
 */
val BarbtoothWurm = card("Barbtooth Wurm") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 6
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Rebecca Guay"
        flavorText = "In its lair lies a carpet of bones."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f627db9-c63e-4353-94f4-3e28db7b222a.jpg?1783946458"
    }
}
