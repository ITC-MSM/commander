package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rootbreaker Wurm
 * {5}{G}{G}
 * Creature — Wurm
 * 6/6
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val RootbreakerWurm = card("Rootbreaker Wurm") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 6
    toughness = 6
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Richard Kane Ferguson"
        flavorText = "As Gerrard made his escape, the wurm covered his flight by helping itself to three great mouthfuls of merfolk."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a686ed6-fc13-4882-b56c-667f556d9804.jpg"
    }
}
