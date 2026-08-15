package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Plated Seastrider
 * {U}{U}
 * Creature — Beast
 * 1/4
 *
 * Vanilla — no rules text.
 */
val PlatedSeastrider = card("Plated Seastrider") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Beast"
    power = 1
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Izzy"
        flavorText = "The Neurok buried entangling cables just under the Quicksilver Sea. One seastrider harvest can provide an army's worth of armor and shields."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97171611-c677-48a6-b081-98a27ecef979.jpg?1783941738"
    }
}
