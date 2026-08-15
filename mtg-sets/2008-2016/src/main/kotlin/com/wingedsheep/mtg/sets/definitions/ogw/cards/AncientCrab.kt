package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancient Crab
 * {1}{U}{U}
 * Creature — Crab
 * 1/5
 *
 * Vanilla — no rules text.
 */
val AncientCrab = card("Ancient Crab") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 1
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Steve Prescott"
        flavorText = "After the fall of Sea Gate and the draining of the Halimar basin, the crab set off to find a new home."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/783794e3-fe0c-4014-ae5a-6c249af23ddc.jpg?1783937919"
    }
}
