package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vestige of Emrakul
 * {3}{R}
 * Creature — Eldrazi Drone
 * 3/4
 * Devoid (This card has no color.)
 * Trample
 */
val VestigeOfEmrakul = card("Vestige of Emrakul") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Eldrazi Drone"
    power = 3
    toughness = 4
    oracleText = "Devoid (This card has no color.)\n" +
        "Trample"

    keywords(Keyword.DEVOID, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Tyler Jacobson"
        flavorText = "Emrakul has not been seen in months. Though her brood's numbers have dwindled in her " +
            "absence, each drone is still a deadly threat."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5d84986-64a1-4bd1-a4f6-3eb147aca357.jpg?1783938196"
    }
}
