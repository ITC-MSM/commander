package com.wingedsheep.mtg.sets.definitions.s99.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Willow Elf
 * {G}
 * Creature — Elf
 * 1/1
 *
 * Vanilla — no rules text.
 */
val WillowElf = card("Willow Elf") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "DiTerlizzi"
        flavorText = "The forest lives in the elf as the elf lives in the forest."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8f0750c-1cce-4088-a848-f11fe3694d89.jpg?1783946018"
    }
}
