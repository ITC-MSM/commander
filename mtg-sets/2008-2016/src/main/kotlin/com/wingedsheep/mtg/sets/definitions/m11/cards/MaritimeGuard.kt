package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Maritime Guard
 * {1}{U}
 * Creature — Merfolk Soldier
 * 1/3
 *
 * Vanilla — no rules text.
 */
val MaritimeGuard = card("Maritime Guard") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Soldier"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Allen Williams"
        flavorText = "Once common in the Kapsho Seas, merfolk were hunted almost to extinction. They survived those dark days and emerged calculating and determined to endure."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f365d82a-88a3-403b-92a6-91c9ccb3421f.jpg?1783941824"
    }
}
