package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Vines
 * {G}
 * Creature — Plant Wall
 * 0/3
 *
 * Defender (This creature can't attack.)
 * Reach (This creature can block creatures with flying.)
 */
val WallOfVines = card("Wall of Vines") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    oracleText = "Defender (This creature can't attack.)\nReach (This creature can block creatures with flying.)"
    power = 0
    toughness = 3

    keywords(Keyword.DEFENDER, Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "John Stanko"
        flavorText = "Like all jungle plants, the vines must fight and claw for sunlight. Once their place is secured, they grow strong, sharp, and impenetrable."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/2050a168-870e-4c47-bc74-bc1c90dd7b3b.jpg?1783941792"
    }
}
