package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alpha Tyrranax
 * {4}{G}{G}
 * Creature — Dinosaur Beast
 * 6/5
 *
 * Vanilla — no rules text.
 */
val AlphaTyrranax = card("Alpha Tyrranax") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur Beast"
    power = 6
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Dave Kendall"
        flavorText = "Hunger seized the tyrranax, and the Sylvok's vision quest ended in disaster."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a2e5279-f28c-4a78-9f8a-16c9f72f8d38.jpg?1783941720"
    }
}
