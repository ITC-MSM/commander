package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of the Tusk
 * {4}{W}{W}
 * Creature — Human Knight
 * 3/7
 *
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val KnightOfTheTusk = card("Knight of the Tusk") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)"
    power = 3
    toughness = 7

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Jesper Ejsing"
        flavorText = "\"Horse? Who needs a horse?\""
        imageUri = "https://cards.scryfall.io/normal/front/2/1/213b4584-420a-48c2-9709-7b07458e914b.jpg?1783934605"
    }
}
