package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Plover Knights
 * {3}{W}{W}
 * Creature — Kithkin Knight
 * 3/3
 * Flying, first strike
 */
val PloverKnights = card("Plover Knights") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Knight"
    power = 3
    toughness = 3
    oracleText = "Flying, first strike"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Quinton Hoover"
        flavorText = "The knights are a major attraction at every Lammastide festival. Teams of riders perform daring feats of flight to the delight of all below."
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea2de26e-4cb3-4d7e-b884-54d6056dc9e9.jpg?1783942911"
    }
}
