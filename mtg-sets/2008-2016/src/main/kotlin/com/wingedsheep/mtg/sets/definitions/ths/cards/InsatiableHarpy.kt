package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Insatiable Harpy
 * {2}{B}{B}
 * Creature — Harpy
 * 2 / 2
 *
 * Flying, lifelink
 */
val InsatiableHarpy = card("Insatiable Harpy") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Harpy"
    power = 2
    toughness = 2
    oracleText = "Flying, lifelink"

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "92"
        artist = "Matt Stewart"
        flavorText = "Gold coin, battered helmet, broken wrist bone—all have the same value in the eyes of a harpy."
        imageUri = "https://cards.scryfall.io/normal/front/1/4/1439ed8d-ae11-4159-9420-5d98c6cc93b3.jpg"
    }
}
