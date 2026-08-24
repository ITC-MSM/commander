package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glacial Wall
 * {2}{U}
 * Creature — Wall
 * 0/7
 *
 * Defender (This creature can't attack.)
 *
 * A vanilla Wall: `Keyword.DEFENDER` is the whole card, and the engine reads it in
 * `AttackRestrictionRules` rather than needing a can't-attack static.
 */
val GlacialWall = card("Glacial Wall") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 7
    oracleText = "Defender (This creature can't attack.)"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Dameon Willich"
        flavorText = "\"We are farther west than any could have imagined possible, but I still wish to press on. Unfortunately, huge walls of ice block further travel. We can't believe they are natural.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07b71bc1-d9a2-4e99-a8fa-cd696925328d.jpg"
    }
}
