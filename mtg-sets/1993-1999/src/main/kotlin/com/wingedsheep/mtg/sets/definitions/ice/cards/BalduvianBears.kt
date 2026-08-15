package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Balduvian Bears
 * {1}{G}
 * Creature — Bear
 * 2/2
 *
 * Vanilla — no rules text.
 */
val BalduvianBears = card("Balduvian Bears") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Quinton Hoover"
        flavorText = "\"They're a hardy bunch, but I'd still bet that they just slept through the worst of the cold times.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef5297cb-e763-4871-9cd3-0e2dbcc52095.jpg?1783947481"
    }
}
