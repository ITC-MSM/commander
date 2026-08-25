package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rustwing Falcon
 * {W}
 * Creature — Bird
 * 1/2
 * Flying
 */
val RustwingFalcon = card("Rustwing Falcon") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 2
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Paul Scott Canavan"
        flavorText = "Native to wide prairies and scrublands, falcons occasionally roost in dragon skeletons."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6691e62-8887-41e8-8e74-76ee2353d45e.jpg"
    }
}
