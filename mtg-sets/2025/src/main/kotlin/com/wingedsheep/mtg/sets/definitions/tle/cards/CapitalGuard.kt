package com.wingedsheep.mtg.sets.definitions.tle.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Capital Guard
 * {1}{R}
 * Creature — Human Soldier
 * 2/2
 *
 * Vanilla — no rules text.
 */
val CapitalGuard = card("Capital Guard") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Nathaniel Himawan"
        flavorText = "Fire Nation guards rely more on fear than fire."
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91cbed11-3b5c-4e7a-9b13-125c1fe5f22f.jpg?1783904780"
    }
}
