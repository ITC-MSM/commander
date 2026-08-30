package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deadly Recluse
 * {1}{G}
 * Creature — Spider
 * 1 / 2
 * Reach (This creature can block creatures with flying.)
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 *
 * Canonical printing: Magic 2010, the card's earliest real printing. Reprinted in Magic 2014.
 */
val DeadlyRecluse = card("Deadly Recluse") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 1
    toughness = 2
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
            "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"
    keywords(Keyword.REACH, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6ab810f1-21d6-4a98-b77a-e455370aa6cc.jpg"
    }
}
