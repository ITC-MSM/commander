package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cobblebrute
 * {3}{R}
 * Creature — Elemental
 * 5/2
 *
 * Vanilla — no rules text.
 */
val Cobblebrute = card("Cobblebrute") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 5
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Eytan Zana"
        flavorText = "The most ancient streets take on a life of their own. A few have decided to move to nicer neighborhoods."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e038376-801f-454e-a635-0e2d58ccbf7c.jpg?1783940356"
    }
}
