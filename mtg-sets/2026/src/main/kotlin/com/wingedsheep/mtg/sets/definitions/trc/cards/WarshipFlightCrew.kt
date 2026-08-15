package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Warship Flight Crew
 * {1}{R}
 * Creature — Klingon Pilot
 * 2/2
 *
 * Vanilla — no rules text.
 */
val WarshipFlightCrew = card("Warship Flight Crew") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Klingon Pilot"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Johann Bodin"
        flavorText = "Opponents write off Klingons as unthinking brutes at their own peril. When the Klingon Empire developed warp drive, humanity still relied on horse-drawn carts."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b6d130a-60f7-410a-b45f-ad81dd203c8c.jpg?1785981141"
    }
}
