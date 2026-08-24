package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kederekt Creeper
 * {U}{B}{R}
 * Creature — Horror
 * 2 / 3
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 *
 * A two-keyword body, no script. It was printed with "can't be blocked except by two or more
 * creatures"; current Oracle errata folds that into [Keyword.MENACE], which is what is authored
 * here, so both abilities are a single `keywords` declaration and the engine's blocking and damage
 * rules carry them.
 */
val KederektCreeper = card("Kederekt Creeper") {
    manaCost = "{U}{B}{R}"
    colorIdentity = "BRU"
    typeLine = "Creature — Horror"
    power = 2
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.MENACE, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Mark Hyzer"
        flavorText = "Bloated with venom, it crawls Grixis looking for victims to ooze onto."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/701498e5-1d4d-42f4-9dd0-5d4cf78f0e68.jpg"
    }
}
