package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bayou Dragonfly
 * {1}{G}
 * Creature — Insect
 * 1/1
 * Flying; swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val BayouDragonfly = card("Bayou Dragonfly") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "Flying; swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.FLYING, Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "215"
        artist = "DiTerlizzi"
        flavorText = "\"Like a sugar stick with wings!\"\n" +
            "—Squee, goblin cabin hand"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93cfcca5-070b-4946-b17b-0c94b1e47fcd.jpg"
    }
}
