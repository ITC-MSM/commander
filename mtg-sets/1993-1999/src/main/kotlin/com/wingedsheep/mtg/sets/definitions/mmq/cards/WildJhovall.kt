package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Jhovall
 * {3}{R}
 * Creature — Cat
 * 3/3
 *
 * Vanilla — no rules text.
 */
val WildJhovall = card("Wild Jhovall") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Daren Bader"
        flavorText = "Jhovalls sharpen their claws on trees—or on Mercadians, if no trees are handy."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64bcc06a-de86-4387-882d-ead33e9c9e01.jpg?1783945930"
    }
}
