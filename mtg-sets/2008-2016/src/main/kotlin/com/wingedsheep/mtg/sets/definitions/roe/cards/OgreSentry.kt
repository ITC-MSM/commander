package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ogre Sentry
 * {1}{R}
 * Creature — Ogre Warrior
 * 3 / 3
 *
 * Defender
 *
 * Modeling notes:
 *  - Vanilla defender; a single `keywords(Keyword.DEFENDER)` declaration covers the printed line.
 */
val OgreSentry = card("Ogre Sentry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 3
    toughness = 3
    oracleText = "Defender"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "159"
        artist = "Eric Deschamps"
        flavorText = "\"You have to appreciate the genius of it. Why bother building defenses when you can just fill the pass with angry ogres?\"\n—Javad Nasrin, Ondu relic hunter"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d71db75c-c896-4887-ba18-92416fa6cbd5.jpg?1783941972"
    }
}
