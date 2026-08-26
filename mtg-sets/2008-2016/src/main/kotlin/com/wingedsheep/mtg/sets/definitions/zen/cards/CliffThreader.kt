package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cliff Threader
 * {1}{W}
 * Creature — Kor Scout
 * 2/1
 * Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)
 */
val CliffThreader = card("Cliff Threader") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Scout"
    power = 2
    toughness = 1
    oracleText = "Mountainwalk (This creature can't be blocked as long as defending player controls a Mountain.)"

    keywords(Keyword.MOUNTAINWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Paul Bonner"
        flavorText = "\"The crossing demands singular focus. Your life consists of these ropes, these hooks, and these rocky crags. Your past is miles below.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/7/1743b625-e937-4cac-8701-9e2dd709a9a4.jpg"
    }
}
