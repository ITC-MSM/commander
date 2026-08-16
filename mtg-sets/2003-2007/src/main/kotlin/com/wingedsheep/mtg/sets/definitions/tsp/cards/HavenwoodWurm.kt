package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Havenwood Wurm
 * {6}{G}
 * Creature — Wurm
 * 5/6
 * Flash (You may cast this spell any time you could cast an instant.)
 * Trample
 */
val HavenwoodWurm = card("Havenwood Wurm") {
    manaCost = "{6}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 5
    toughness = 6
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\nTrample"

    keywords(Keyword.FLASH, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Stuart Griffin"
        flavorText = "Time rifts bring tunneling beasts of old into the hard, dry earth of the present. Most die there, trapped, but the mightiest burst through the surface."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/561a4bb5-285a-4d52-b372-d165e442cff3.jpg?1783943212"
    }
}
