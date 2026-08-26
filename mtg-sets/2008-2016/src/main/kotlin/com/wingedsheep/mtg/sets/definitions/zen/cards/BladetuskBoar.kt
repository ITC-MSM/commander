package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bladetusk Boar
 * {3}{R}
 * Creature — Boar
 * 3/2
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 */
val BladetuskBoar = card("Bladetusk Boar") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Boar"
    power = 3
    toughness = 2
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)"

    keywords(Keyword.INTIMIDATE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Paul Bonner"
        flavorText = "Those who dare stand in its path are either brave or mindless. Or in the case of goblins, both."
        imageUri = "https://cards.scryfall.io/normal/front/1/5/1558dfaf-15ed-4220-9051-bf0bf442b2e9.jpg"
    }
}
