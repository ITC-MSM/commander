package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wild Ox
 * {3}{G}
 * Creature — Ox
 * 3/3
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val WildOx = card("Wild Ox") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ox"
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"
    power = 3
    toughness = 3
    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "150"
        artist = "Jeffrey R. Busch"
        flavorText = "It has the run of the swamps, and it knows it."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c5a7890-86dc-4fb8-a47b-99331bbe7c29.jpg"
    }
}
