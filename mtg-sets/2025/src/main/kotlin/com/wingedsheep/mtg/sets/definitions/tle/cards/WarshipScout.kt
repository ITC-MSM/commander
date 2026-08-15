package com.wingedsheep.mtg.sets.definitions.tle.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Warship Scout
 * {R}
 * Creature — Human Scout
 * 2/1
 *
 * Vanilla — no rules text.
 */
val WarshipScout = card("Warship Scout") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "244"
        artist = "Brandon L. Hunt"
        flavorText = "\"For the glory of the Fire Nation!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1a95982-be16-465a-9c1b-1f4d875c0c40.jpg?1783904777"
    }
}
