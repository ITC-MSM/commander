package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Resistance Skywarden
 * {3}{R}{R}
 * Creature — Ogre Rebel
 * 5/5
 *
 * Reach (This creature can block creatures with flying.)
 * Menace (This creature can't be blocked except by two or more creatures.)
 */
val ResistanceSkywarden = card("Resistance Skywarden") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Rebel"
    power = 5
    toughness = 5
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "Menace (This creature can't be blocked except by two or more creatures.)"

    keywords(Keyword.REACH, Keyword.MENACE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Jarel Threat"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/6249aabe-8f21-4257-9e04-ceffd44d42a5.jpg?1783918024"
    }
}
