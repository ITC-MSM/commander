package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lost Soul
 * {1}{B}{B}
 * Creature — Spirit Minion
 * 2/1
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val LostSoul = card("Lost Soul") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit Minion"
    power = 2
    toughness = 1
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Randy Asplund-Faith"
        flavorText = "She walks in the twilight, her steps make no sound,/ Her feet leave no tracks on the " +
            "dew-covered ground./ Her hand gently beckons, she whispers your name—/ But those who go " +
            "with her are never the same."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/601eed5c-436d-425b-a45f-07881ad893c8.jpg?1783948064"
    }
}
