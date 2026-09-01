package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Breakneck Berserker
 * {2}{R}
 * Creature — Dwarf Berserker
 * 3/2
 * Haste
 * */
val BreakneckBerserker = card("Breakneck Berserker") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Berserker"
    oracleText = "Haste"
    power = 3
    toughness = 2

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Scott Murphy"
        flavorText = "\"Go for the knees! The last giant who tried to get past us got rolled back down the mountain in a dozen pieces.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6eb23c9-6061-4ad1-a8f3-2c791c49f352.jpg"
    }
}
