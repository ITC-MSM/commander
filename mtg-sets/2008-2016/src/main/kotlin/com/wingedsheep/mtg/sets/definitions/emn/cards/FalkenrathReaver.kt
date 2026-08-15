package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Falkenrath Reaver
 * {1}{R}
 * Creature — Vampire
 * 2/2
 *
 * Vanilla — no rules text.
 */
val FalkenrathReaver = card("Falkenrath Reaver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Daarken"
        flavorText = "\"Each day there are new reports of homes along Getander Pass and Hofsaddel falling to vampires of a more savage nature. I've even heard tell that Anje Falkenrath has returned. Please send whatever help you can.\"\n—Sterin Gorn, letter to Thalia"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd2023b3-5999-44b1-a67f-3f2a76cb2d14.jpg?1783937462"
    }
}
