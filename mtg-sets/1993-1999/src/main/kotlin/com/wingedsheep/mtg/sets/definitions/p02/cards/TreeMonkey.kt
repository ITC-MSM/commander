package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tree Monkey
 * {G}
 * Creature — Monkey
 * 1/1
 * Reach (This creature can block creatures with flying.)
 */
val TreeMonkey = card("Tree Monkey") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Monkey"
    oracleText = "Reach (This creature can block creatures with flying.)"
    power = 1
    toughness = 1
    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Una Fricker"
        flavorText = "\"They serve the world best on a platter with shallots and onions.\"\n—Talas sailor"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c60bbbf7-a005-4b4b-b8e4-e95bbb67f529.jpg"
    }
}
