package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Bedhead Beastie
 * {4}{R}{R}
 * Creature — Beast
 * 5/6
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card,
 * reveal it, put it into your hand, then shuffle.)
 */
val BedheadBeastie = card("Bedhead Beastie") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 6
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card, " +
        "reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.MENACE)
    keywordAbility(KeywordAbility.typecycling("Mountain", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "David Auden Nash"
        flavorText = "It had heard monsters were supposed to hide under beds, and it did its best."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77c72b5b-dd81-4eb4-80d9-a0124e27e1dc.jpg?1726286314"
    }
}
