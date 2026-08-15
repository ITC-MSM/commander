package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Douser of Lights
 * {4}{B}
 * Creature — Horror
 * 4/5
 *
 * Vanilla — no rules text.
 */
val DouserOfLights = card("Douser of Lights") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Darek Zabrocki"
        flavorText = "The party of Rakdos revelers cackled and capered as the thing approached. It hissed, and they jabbed their torches at it, giggling when it recoiled. Then, one by one, the torches went out—and the screaming began."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c554be7-6fd4-4642-aaa0-2781d9c388e4.jpg?1783934175"
    }
}
