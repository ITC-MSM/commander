package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hillcomber Giant
 * {2}{W}{W}
 * Creature — Giant Scout
 * 3/3
 * Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)
 */
val HillcomberGiant = card("Hillcomber Giant") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant Scout"
    power = 3
    toughness = 3
    oracleText = "Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)"

    keywords(Keyword.MOUNTAINWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Ralph Horsley"
        flavorText = "The giants believe the fossils they find in Lorwyn's rocky heights are dreams frozen in time, and they treasure them."
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0a785a2-462d-4321-9e48-eb98698b7591.jpg?1783942914"
    }
}
