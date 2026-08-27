package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thundersteel Colossus — Kamigawa: Neon Dynasty #261 (canonical printing)
 * {7} · Artifact — Vehicle · 7/7
 *
 * Trample, haste
 * Crew 2
 *
 * Haste on a Vehicle is not redundant: crewing it the turn it enters would otherwise leave it
 * summoning-sick, since it has been under your control only since this turn (CR 302.6).
 */
val ThundersteelColossus = card("Thundersteel Colossus") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    power = 7
    toughness = 7
    oracleText = "Trample, haste\nCrew 2 (Tap any number of creatures you control with total " +
        "power 2 or more: This Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.TRAMPLE, Keyword.HASTE)
    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "261"
        artist = "Victor Adame Minguez"
        flavorText = "\"*This* is what I needed all that alloy for.\"\n—Arima, to Emire"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7d49f9c-2dd3-4aed-9b78-e1bcad354a35.jpg?1783923820"
    }
}
