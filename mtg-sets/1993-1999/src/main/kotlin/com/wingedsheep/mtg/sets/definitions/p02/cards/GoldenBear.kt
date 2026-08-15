package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Golden Bear
 * {3}{G}
 * Creature — Bear
 * 4/3
 *
 * Vanilla — no rules text.
 */
val GoldenBear = card("Golden Bear") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Una Fricker"
        flavorText = "The elvish scout was scrupulous about the truth. She told the traders that gold lay nearby."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7dfc789-7ea0-4eb8-8c3b-2c50fd52cbab.jpg?1783946455"
    }
}
