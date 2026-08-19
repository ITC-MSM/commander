package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Knight of Meadowgrain
 * {W}{W}
 * Creature — Kithkin Knight
 * 2/2
 * First strike
 * Lifelink
 */
val KnightOfMeadowgrain = card("Knight of Meadowgrain") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Knight"
    power = 2
    toughness = 2
    oracleText = "First strike\nLifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.FIRST_STRIKE, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Larry MacDougall"
        flavorText = "\"By tradition, we don't speak for two days after battle. If our deeds won't speak for themselves, what else could be said?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb33901-1f97-4080-a9dd-922ef29f42c9.jpg?1783942912"
    }
}
