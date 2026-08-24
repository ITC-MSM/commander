package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fortified Rampart
 * {1}{W}
 * Creature — Wall
 * 0/6
 * Defender
 */
val FortifiedRampart = card("Fortified Rampart") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 6
    oracleText = "Defender"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "David Gaillet"
        flavorText = "The refuge's defenses allow new recruits to see lesser Eldrazi up close, steeling their " +
            "stomachs for what's to come."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/5095e2ab-a7f5-45bc-8b2f-31198ea27bba.jpg?1783938220"
    }
}
