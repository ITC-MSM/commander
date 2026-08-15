package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragon-Style Twins
 * {3}{R}{R}
 * Creature — Human Monk
 * 3/3
 * Double strike
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val DragonStyleTwins = card("Dragon-Style Twins") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Monk"
    power = 3
    toughness = 3
    oracleText = "Double strike\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    keywords(Keyword.DOUBLE_STRIKE)
    prowess()

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "108"
        artist = "Wesley Burt"
        flavorText = "\"We are the flicker of the flame and its heat, the two sides of a single blade.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/646edf58-4bc5-4d87-af68-b8b006ccb806.jpg?1562787685"
    }
}
