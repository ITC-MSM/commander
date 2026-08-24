package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hostile Minotaur
 * {3}{R}
 * Creature — Minotaur
 * 3/3
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 */
val HostileMinotaur = card("Hostile Minotaur") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur"
    power = 3
    toughness = 3
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)"

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Joe Slucher"
        flavorText = "The bellow of a minotaur always translates to \"charge.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad6d0a11-3a9c-4de0-9a46-06d2b9356eb7.jpg"
    }
}
