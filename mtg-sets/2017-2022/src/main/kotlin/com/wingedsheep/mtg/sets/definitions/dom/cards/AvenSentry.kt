package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Aven Sentry
 * {3}{W}
 * Creature — Bird Soldier
 * 3/2
 * Flying
 */
val AvenSentry = card("Aven Sentry") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Soldier"
    power = 3
    toughness = 2
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Dan Murayama Scott"
        flavorText = "\"My flock flew from a distant continent ruined by cataclysm and war. Benalia gave us shelter to end our exodus.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf49e5bf-07fb-44b0-8e74-092088d9019f.jpg?1562742173"
    }
}
