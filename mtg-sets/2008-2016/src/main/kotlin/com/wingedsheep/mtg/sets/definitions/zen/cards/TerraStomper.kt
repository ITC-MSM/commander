package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Terra Stomper
 * {3}{G}{G}{G}
 * Creature — Beast
 * 8/8
 * This spell can't be countered.
 * Trample
 *
 * "This spell can't be countered" is a characteristic of the *spell*, so it rides `cantBeCountered`
 * on the card rather than a static ability on the permanent.
 *
 * Canonical printing: Zendikar, the card's earliest real-expansion printing. Reprinted in M15 as a
 * `Printing` row.
 */
val TerraStomper = card("Terra Stomper") {
    manaCost = "{3}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 8
    toughness = 8
    oracleText =
        "This spell can't be countered.\n" +
        "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    cantBeCountered = true

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "187"
        artist = "Goran Josic"
        flavorText = "Sometimes violent earthquakes, hurtling boulders, and unseasonable dust storms are wrongly attributed to the Roil."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ab062f4-e4b1-4129-9027-d0ca1a723273.jpg?1783942130"
    }
}
