package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Farbog Explorer
 * {2}{W}
 * Creature — Human Scout
 * 2 / 3
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val FarbogExplorer = card("Farbog Explorer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 3
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Scott Chou"
        flavorText = "\"I'd slog through a thousand swamps to help one soul find rest.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/8/489c6a2f-38b4-4ff9-95f7-431384480ed9.jpg?1783940735"
    }
}
