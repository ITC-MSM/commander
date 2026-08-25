package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dusk Legion Dreadnought
 * {5}
 * Artifact — Vehicle
 * 4/6
 *
 * Vigilance
 * Crew 2
 */
val DuskLegionDreadnought = card("Dusk Legion Dreadnought") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    oracleText = "Vigilance\n" +
        "Crew 2 (Tap any number of creatures you control with total power 2 or more: " +
        "This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 6

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Titus Lunter"
        flavorText = "It loomed up over the horizon, silent and dark as a grave."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aec26144-9acf-43b7-8614-1a2952df613d.jpg"
    }
}
