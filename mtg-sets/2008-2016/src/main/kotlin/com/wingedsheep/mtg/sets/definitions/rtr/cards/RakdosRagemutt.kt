package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rakdos Ragemutt
 * {3}{B}{R}
 * Creature — Elemental Dog
 * 3/3
 *
 * Lifelink, haste
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val RakdosRagemutt = card("Rakdos Ragemutt") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Elemental Dog"
    oracleText = "Lifelink, haste"
    power = 3
    toughness = 3

    keywords(Keyword.LIFELINK, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Ryan Barger"
        flavorText = "Ragemutts pull the chariots of the Butcher Clowns, a trio of wingless, zombified faeries formerly of the Izzet."
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb36840a-3f85-4fca-87ab-379dfce8e542.jpg?1783940334"
    }
}
