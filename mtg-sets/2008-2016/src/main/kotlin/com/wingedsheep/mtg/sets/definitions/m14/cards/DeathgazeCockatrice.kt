package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deathgaze Cockatrice
 * {2}{B}{B}
 * Creature — Cockatrice
 * 2 / 2
 * Flying
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val DeathgazeCockatrice = card("Deathgaze Cockatrice") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Cockatrice"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
            "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Kev Walker"
        flavorText = "\"Sometimes I come across a stone finger or foot and I know I'm in cockatrice territory.\"\n" +
            "—Rulak, bog guide"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f17b58c-9738-4cdb-a408-e1595c384b92.jpg"
    }
}
