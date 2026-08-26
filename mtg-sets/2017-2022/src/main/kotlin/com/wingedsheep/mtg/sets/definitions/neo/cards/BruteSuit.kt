package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Brute Suit — Kamigawa: Neon Dynasty #241 (canonical printing)
 * {3} · Artifact — Vehicle · 4/3
 *
 * Vigilance
 * Crew 1
 */
val BruteSuit = card("Brute Suit") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    power = 4
    toughness = 3
    oracleText = "Vigilance\nCrew 1 (Tap any number of creatures you control with total power 1 " +
        "or more: This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Raymond Swanland"
        flavorText = "\"Send them a message they can't ignore.\"\n—Satoru Umezawa"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/363cb43a-e358-4380-a42f-9b095ca522c6.jpg?1783923827"
    }
}
