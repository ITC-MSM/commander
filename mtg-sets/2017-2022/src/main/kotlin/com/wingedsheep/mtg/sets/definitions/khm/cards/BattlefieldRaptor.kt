package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Battlefield Raptor
 * {W}
 * Creature — Bird
 * 1/2
 * Flying, first strike
 * */
val BattlefieldRaptor = card("Battlefield Raptor") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    oracleText = "Flying, first strike"
    power = 1
    toughness = 2

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Mike Bierek"
        flavorText = "It wheeled upward, away from the shrieks and thunder. It reached the point where sky met smoke, and, with but a glance at the horizon, aimed itself and dove."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/389f0045-218d-41cd-bdca-8a9a0ab1b31b.jpg"
    }
}
