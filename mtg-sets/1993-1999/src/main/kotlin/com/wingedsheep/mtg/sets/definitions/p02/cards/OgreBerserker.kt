package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Berserker
 * {4}{R}
 * Creature — Ogre Berserker
 * 4/2
 * Haste
 */
val OgreBerserker = card("Ogre Berserker") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Berserker"
    oracleText = "Haste"
    power = 4
    toughness = 2
    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "David A. Cherry"
        flavorText = "The machine does the growling, so the ogre's free to smile."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6969d4d-c311-4663-bcd6-77a4d6458335.jpg"
    }
}
