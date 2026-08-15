package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Slinking Serpent
 * {2}{U}{B}
 * Creature — Serpent
 * 2/3
 * Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)
 */
val SlinkingSerpent = card("Slinking Serpent") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Serpent"
    power = 2
    toughness = 3
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)"

    keywords(Keyword.FORESTWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "274"
        artist = "Wayne England"
        flavorText = "It winds its way through undergrowth as easily as it swims through shallows."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/070a7004-5a28-4ccb-8640-ad6b07b51ece.jpg?1562896404"
    }
}
