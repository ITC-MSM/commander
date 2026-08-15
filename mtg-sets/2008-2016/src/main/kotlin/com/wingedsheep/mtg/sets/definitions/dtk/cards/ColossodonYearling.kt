package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Colossodon Yearling
 * {2}{G}
 * Creature — Beast
 * 2/4 vanilla
 */
val ColossodonYearling = card("Colossodon Yearling") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Yeong-Hao Han"
        flavorText = "The colossodon's hard outer shell stops many predators, but with a gentle flip from a dragon, it quickly becomes a meal in a bowl."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2c60e63-0b86-4100-a932-bb9e9b197610.jpg?1562795540"
    }
}
