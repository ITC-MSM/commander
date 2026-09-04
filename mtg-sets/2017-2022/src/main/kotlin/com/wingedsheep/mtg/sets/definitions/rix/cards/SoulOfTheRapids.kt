package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soul of the Rapids
 * {3}{U}{U}
 * Creature — Elemental
 * 3/2
 * Flying
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val SoulOfTheRapids = card("Soul of the Rapids") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    oracleText = "Flying\nHexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 3
    toughness = 2

    keywords(Keyword.FLYING, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Anthony Palumbo"
        flavorText = "With Kumena in control of the Immortal Sun, the rapids rose from their riverbeds and the waterfalls took flight."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a1aed7d-4236-4d44-9366-ee03e15469bc.jpg?1783935317"
    }
}
