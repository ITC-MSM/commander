package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sun Sentinel
 * {1}{W}
 * Creature — Human Soldier
 * 2/2
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val SunSentinel = card("Sun Sentinel") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 2
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "James Ryman"
        flavorText = "\"I will not sleep until Orazca is ours once more.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/8/6867fd5a-3bbe-416d-96a6-1db0b341260e.jpg"
    }
}
