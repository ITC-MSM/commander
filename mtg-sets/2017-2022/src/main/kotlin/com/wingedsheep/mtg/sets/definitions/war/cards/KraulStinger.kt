package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kraul Stinger
 * {2}{G}
 * Creature — Insect Assassin
 * 2/2
 * Deathtouch
 */
val KraulStinger = card("Kraul Stinger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Assassin"
    oracleText = "Deathtouch"
    power = 2
    toughness = 2

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Randy Vargas"
        flavorText = "He listens to the sounds of battle filtering down from above, waiting for silence, waiting for his time to claim the streets."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46b88fe9-2450-47ee-ac1e-bbbccbf5684f.jpg"
    }
}
