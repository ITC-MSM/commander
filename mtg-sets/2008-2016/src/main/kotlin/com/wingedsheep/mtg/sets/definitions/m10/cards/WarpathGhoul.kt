package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Warpath Ghoul
 * {2}{B}
 * Creature — Zombie
 * 3/2
 *
 * Vanilla — no rules text.
 */
val WarpathGhoul = card("Warpath Ghoul") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "rk post"
        flavorText = "The battle was over, but the carnage continued for days."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c6cc262-ba0c-4cca-ae9c-24a1824753e4.jpg?1783942377"
    }
}
