package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragon's Eye Sentry
 * {W}
 * Creature — Human Monk
 * 1/3
 * Defender, first strike
 */
val DragonsEyeSentry = card("Dragon's Eye Sentry") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Monk"
    power = 1
    toughness = 3
    oracleText = "Defender, first strike"

    keywords(Keyword.DEFENDER, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/750880cd-59bf-4b67-a2d5-9b66e4d05665.jpg?1562788403"
    }
}
