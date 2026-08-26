package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bog Tatters
 * {4}{B}
 * Creature — Wraith
 * 4/2
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val BogTatters = card("Bog Tatters") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Wraith"
    power = 4
    toughness = 2
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Daarken"
        flavorText = "A wraith is a tale of brutal slaying told anew whenever it finds a victim."
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0830c00-02fa-4fc8-907c-19d4b7f9cd6e.jpg"
    }
}
