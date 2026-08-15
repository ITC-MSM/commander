package com.wingedsheep.mtg.sets.definitions.anb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shorecomber Crab
 * {U}
 * Creature — Crab
 * 0/4
 *
 * Vanilla — no rules text.
 */
val ShorecomberCrab = card("Shorecomber Crab") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 0
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32a"
        artist = "Chris Seaman"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0d899e1-bded-4e16-8ecc-cc07784af4bb.jpg?1783929831"
    }
}
