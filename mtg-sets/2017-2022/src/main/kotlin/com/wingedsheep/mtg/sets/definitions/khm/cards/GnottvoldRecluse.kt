package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnottvold Recluse
 * {2}{G}
 * Creature — Spider
 * 4/2
 * Reach
 * */
val GnottvoldRecluse = card("Gnottvold Recluse") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    oracleText = "Reach"
    power = 4
    toughness = 2

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Nicholas Gregory"
        flavorText = "Fed up with webs impeding travelers in other realms, Kolvori banished the giant spiders to Gnottvold, where their webs snare only the occasional rampaging troll."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af46c8c8-5dfa-4ebb-b0b9-cd25d01dd432.jpg"
    }
}
