package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Griffin Sentinel
 * {2}{W}
 * Creature — Griffin
 * 1 / 3
 * Flying
 * Vigilance (Attacking doesn't cause this creature to tap.)
 *
 * Canonical printing: Magic 2010, the card's earliest real printing. Reprinted in Magic 2014.
 */
val GriffinSentinel = card("Griffin Sentinel") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
            "Vigilance (Attacking doesn't cause this creature to tap.)"
    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Warren Mahy"
        flavorText = "Once a griffin sentinel adopts a territory as its own, only death can force it to betray its post."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/6784b663-b117-45a2-bde4-72e080058ea7.jpg"
    }
}
