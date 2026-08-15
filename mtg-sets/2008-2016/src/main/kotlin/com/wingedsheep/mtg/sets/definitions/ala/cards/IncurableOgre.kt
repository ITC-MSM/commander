package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Incurable Ogre
 * {3}{R}
 * Creature — Ogre Mutant
 * 5/1
 *
 * Vanilla — no rules text.
 */
val IncurableOgre = card("Incurable Ogre") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Mutant"
    power = 5
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Carl Critchlow"
        flavorText = "Each mutation causes the incurables to look vastly different from one another. They are left with only one thing in common: their insatiable lust for the slaughter."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ad3381e-ae2f-40cf-8a7b-62375e9f453e.jpg?1783942560"
    }
}
