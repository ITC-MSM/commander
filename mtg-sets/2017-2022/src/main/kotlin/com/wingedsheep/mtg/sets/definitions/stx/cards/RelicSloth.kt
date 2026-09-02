package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Relic Sloth — Strixhaven: School of Mages #223 (canonical printing)
 * {3}{R}{W} · Creature — Sloth Beast · 4/4
 *
 * Vigilance
 * Menace (This creature can't be blocked except by two or more creatures.)
 *
 * Two plain evergreen keywords, nothing else — both are simple [Keyword] markers the engine
 * reads directly.
 */
val RelicSloth = card("Relic Sloth") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Sloth Beast"
    oracleText =
        "Vigilance\n" +
        "Menace (This creature can't be blocked except by two or more creatures.)"
    power = 4
    toughness = 4

    keywords(Keyword.VIGILANCE, Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "223"
        artist = "Ilse Gort"
        flavorText = "When it comes to transporting priceless, delicate artifacts, safety is more important than speed."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1cb483f-c567-4cfd-9fe8-1503e7b40542.jpg?1783927297"
    }
}
