package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soltari Foot Soldier
 * {W}
 * Creature — Soltari Soldier
 * 1/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 */
val SoltariFootSoldier = card("Soltari Foot Soldier") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Soldier"
    power = 1
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)"

    keywords(Keyword.SHADOW)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Janet Aulisio"
        flavorText = "\"Children of the Ruins, raised to be warriors, know that life begins when another speaks their names.\"\n" +
            "—Soltari *Tales of Life*"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdf295dc-72df-4097-b767-d89ab807bf2e.jpg"
    }
}
