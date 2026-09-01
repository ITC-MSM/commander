package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tenement Crasher
 * {5}{R}
 * Creature — Beast
 * 5/4
 *
 * Haste
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else.
 */
val TenementCrasher = card("Tenement Crasher") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    oracleText = "Haste"
    power = 5
    toughness = 4

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "Warren Mahy"
        flavorText = "Nothing was going to stop it—not the narrow alleys, not the Boros garrison, and certainly not the four-story Orzhov cathedral."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44af9170-bd99-4fde-b673-62d988312b2d.jpg?1783940353"
    }
}
