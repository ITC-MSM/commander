package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feral Abomination
 * {5}{B}
 * Creature — Thrull
 * 5/5
 * Deathtouch
 */
val FeralAbomination = card("Feral Abomination") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Thrull"
    power = 5
    toughness = 5
    oracleText = "Deathtouch"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "92"
        artist = "Darek Zabrocki"
        flavorText = "\"Urborg used to be lovely—scenic volcanoes, respectable lich lords. Since the Cabal came with their nightmares and thrulls, it's all gone to the worms.\" —Mister Lostspoons"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3292e262-4409-4f31-9de2-a232817a0734.jpg?1591101228"
    }
}
