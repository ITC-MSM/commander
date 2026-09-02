package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Twinscroll Shaman — Strixhaven: School of Mages #118 (canonical printing)
 * {2}{R} · Creature — Dwarf Shaman · 1/2
 *
 * Double strike
 *
 * A vanilla-plus-keyword creature: the printed keyword is the whole script.
 */
val TwinscrollShaman = card("Twinscroll Shaman") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Shaman"
    oracleText =
        "Double strike"
    power = 1
    toughness = 2

    keywords(Keyword.DOUBLE_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Chris Seaman"
        flavorText = "\"May your wretched name never grace the annals of history, you ignorant buffoon!\""
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87193af5-4b6b-48d0-9b75-8171bb1d6e53.jpg?1783927350"
    }
}
