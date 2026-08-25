package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sleek Schooner
 * {3}
 * Artifact — Vehicle
 * 4/3
 *
 * Crew 1
 */
val SleekSchooner = card("Sleek Schooner") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    oracleText = "Crew 1 (Tap any number of creatures you control with total power 1 or more: " +
        "This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 3

    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "247"
        artist = "Mark Winters"
        flavorText = "The pirates had left the open sea behind, but they were still in their element: reckless adventure."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/767e2bfb-bcf5-442c-b092-bdb1f4f13561.jpg"
    }
}
