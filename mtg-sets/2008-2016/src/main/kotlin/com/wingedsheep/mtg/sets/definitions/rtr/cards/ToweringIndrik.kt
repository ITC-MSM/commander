package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Towering Indrik
 * {3}{G}
 * Creature — Beast
 * 2/4
 *
 * Reach (This creature can block creatures with flying.)
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else.
 */
val ToweringIndrik = card("Towering Indrik") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "Reach (This creature can block creatures with flying.)"
    power = 2
    toughness = 4

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Lars Grant-West"
        flavorText = "It chases its airborne prey relentlessly, heedless to what it pulverizes beneath its hooves."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6049e92-6c52-44be-a3c7-aa8e8bf9c10a.jpg?1783940346"
    }
}
