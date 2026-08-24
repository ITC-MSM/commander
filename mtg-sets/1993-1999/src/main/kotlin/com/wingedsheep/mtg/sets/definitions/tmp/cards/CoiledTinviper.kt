package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coiled Tinviper
 * {3}
 * Artifact Creature — Snake
 * 2/1
 * First strike
 */
val CoiledTinviper = card("Coiled Tinviper") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Snake"
    power = 2
    toughness = 1
    oracleText = "First strike"

    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "279"
        artist = "John Matson"
        flavorText = "The bite of the tinviper feels most like a razor drawn across the tongue."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/426a28bd-033d-41af-b577-ece73cbd7b3a.jpg"
    }
}
