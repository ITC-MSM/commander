package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sanctuary Cat
 * {W}
 * Creature — Cat
 * 1/2
 *
 * Vanilla — no rules text.
 */
val SanctuaryCat = card("Sanctuary Cat") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "David Palumbo"
        flavorText = "Cats prowl the corridors of Avacyn's churches in search of devils or signs of their mischief."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96865440-01ad-40f2-90d7-9ecd0b4efecc.jpg?1783940850"
    }
}
