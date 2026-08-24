package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dauthi Marauder
 * {2}{B}
 * Creature — Dauthi Minion
 * 3/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 */
val DauthiMarauder = card("Dauthi Marauder") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Dauthi Minion"
    power = 3
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)"

    keywords(Keyword.SHADOW)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Andrew Robinson"
        flavorText = "\"The Dauthi came from beneath the Ruins one night, and the darkness cast them in the best possible light.\"\n" +
            "—Soltari *Tales of Life*"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee847d84-ec8d-4ec3-8436-68d6f144e22f.jpg"
    }
}
