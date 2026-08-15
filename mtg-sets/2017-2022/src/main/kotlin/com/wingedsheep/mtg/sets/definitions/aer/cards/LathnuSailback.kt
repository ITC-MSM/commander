package com.wingedsheep.mtg.sets.definitions.aer.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lathnu Sailback
 * {4}{R}
 * Creature — Lizard
 * 5/4
 *
 * Vanilla — no rules text.
 */
val LathnuSailback = card("Lathnu Sailback") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    power = 5
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Christopher Burdett"
        flavorText = "Travelers to Lathnu, high on the Devra Cliffs, need not fear the political strife of Ghirapur . . . but they have other dangers to worry about."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33998799-f31b-4522-93b2-0c34c570ebf7.jpg?1783936752"
    }
}
