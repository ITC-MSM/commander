package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sagu Mauler
 * {4}{G}{U}
 * Creature — Beast
 * 6/6
 * Trample, hexproof
 * Morph {3}{G}{U}
 */
val SaguMauler = card("Sagu Mauler") {
    manaCost = "{4}{G}{U}"
    colorIdentity = "UG"
    typeLine = "Creature — Beast"
    power = 6
    toughness = 6
    oracleText = "Trample, hexproof\nMorph {3}{G}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)"

    keywords(Keyword.TRAMPLE, Keyword.HEXPROOF)

    morph = "{3}{G}{U}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "196"
        artist = "Raymond Swanland"
        flavorText = "The Sagu Jungle's thick undergrowth conceals even the largest predators—if they wish to remain hidden."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c64af58-963d-497b-ab95-104839d96b94.jpg?1562786271"
    }
}
