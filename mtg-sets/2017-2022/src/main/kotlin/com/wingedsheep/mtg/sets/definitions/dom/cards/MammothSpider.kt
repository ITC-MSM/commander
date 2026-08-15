package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mammoth Spider
 * {4}{G}
 * Creature — Spider
 * 3/5
 * Reach
 */
val MammothSpider = card("Mammoth Spider") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 3
    toughness = 5
    oracleText = "Reach (This creature can block creatures with flying.)"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Lars Grant-West"
        flavorText = "Most spiders of Llanowar disdain elvish alliances. No elf has as many beautiful eyes or as many strong arms."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db18bbcd-527d-4134-a6ae-6f4381419867.jpg?1562743978"
    }
}
