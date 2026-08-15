package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancient Carp
 * {4}{U}
 * Creature — Fish
 * 2/5
 *
 * Vanilla creature — no abilities.
 */
val AncientCarp = card("Ancient Carp") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Christopher Burdett"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fef6e95-e7f1-4646-be5e-130c8b5a3ca6.jpg?1562783481"
    }
}
