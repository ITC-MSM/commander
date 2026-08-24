package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nightveil Predator
 * {U}{U}{B}{B}
 * Creature — Vampire
 * 3/3
 * Flying, deathtouch
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val NightveilPredator = card("Nightveil Predator") {
    manaCost = "{U}{U}{B}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Vampire"
    oracleText = "Flying, deathtouch\n" +
        "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "191"
        artist = "Darek Zabrocki"
        flavorText = "\"Three daggers left in an angel's back, three enforcers with memory loss, three keys stolen from my own belt—and you talk of peace?\"\n—Tajic, to Aurelia"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/193289c9-837d-4d18-9ef1-720e8e335e62.jpg?1783934125"
    }
}
