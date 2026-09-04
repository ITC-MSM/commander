package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sporecap Spider
 * {2}{G}
 * Creature — Spider
 * 1 / 5
 *
 * Reach
 *
 * Modeling notes:
 *  - Vanilla reach creature; a single `keywords(Keyword.REACH)` declaration covers the printed line.
 */
val SporecapSpider = card("Sporecap Spider") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 1
    toughness = 5
    oracleText = "Reach"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "209"
        artist = "Lars Grant-West"
        flavorText = "\"They don't move much, but then again, if you get caught in its web, it has all the time in the world to get to you.\"\n—Saidah, Joraga hunter"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abb1d18f-7a94-4a2f-a60c-0af852d44501.jpg?1783941960"
    }
}
