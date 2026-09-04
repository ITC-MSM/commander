package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gloomhunter
 * {2}{B}
 * Creature — Bat
 * 2 / 1
 *
 * Flying
 *
 * Modeling notes:
 *  - Vanilla flyer; a single `keywords(Keyword.FLYING)` declaration covers the printed line.
 */
val Gloomhunter = card("Gloomhunter") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bat"
    power = 2
    toughness = 1
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Lars Grant-West"
        flavorText = "Scavengers both mundane and magical follow in its wake, feeding on the scraps of flesh and spirit it leaves behind."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98db4317-9850-44c1-884b-d8d3abe1afeb.jpg?1783941985"
    }
}
