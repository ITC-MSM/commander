package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sedge Scorpion
 * {G}
 * Creature — Scorpion
 * 1 / 1
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val SedgeScorpion = card("Sedge Scorpion") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Scorpion"
    power = 1
    toughness = 1
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "John Stanko"
        flavorText = "Thakolides the Mighty\nSlayer of minotaurs\nVanquisher of giants\nKilled by a scorpion\n—Inscription on an Akroan grave"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66733fcb-fddc-4c15-bc51-c2cd42ac5a70.jpg"
    }
}
