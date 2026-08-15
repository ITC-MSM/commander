package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leopard-Spotted Jiao
 * {1}{R}
 * Creature — Beast
 * 3/1
 *
 * Vanilla — no rules text.
 */
val LeopardSpottedJiao = card("Leopard-Spotted Jiao") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Shinchuen Chen"
        flavorText = "This strange beast has the hide of a leopard, the howl of a dog, and the horns of an ox."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91df110f-85d2-41cb-96b6-6c79cebfada7.jpg?1783934627"
    }
}
