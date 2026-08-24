package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Whitesun's Passage — Scars of Mirrodin #27
 * {1}{W} · Instant
 *
 * You gain 5 life.
 */
val WhitesunsPassage = card("Whitesun's Passage") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "You gain 5 life."

    spell {
        effect = Effects.GainLife(5)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "John Avon"
        flavorText = "All over the Razor Fields, Whitesun is celebrated. Even the followers of the rebel Juryan, far from the Cave of Light, bow their heads in reverence."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a74d1bf3-4630-4be0-af5f-590789d27a0c.jpg?1783941740"
    }
}
