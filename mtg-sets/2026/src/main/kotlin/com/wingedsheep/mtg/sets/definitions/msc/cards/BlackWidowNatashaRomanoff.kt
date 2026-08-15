package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Black Widow, Natasha Romanoff
 * {1}{R}
 * Legendary Creature — Human Assassin Hero
 * 2/2
 *
 * Vanilla — no rules text.
 */
val BlackWidowNatashaRomanoff = card("Black Widow, Natasha Romanoff") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Assassin Hero"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "846"
        artist = "Julia Vasilyeva"
        flavorText = "\"They tell me now that I'm an Avenger I can't kill anyone. You want to put that to the test?\""
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f3e2f33-9c31-4107-b9b4-a7e1d2d43bab.jpg?1783902994"
    }
}
