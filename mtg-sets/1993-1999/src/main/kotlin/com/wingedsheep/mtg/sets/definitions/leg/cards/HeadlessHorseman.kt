package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Headless Horseman
 * {2}{B}
 * Creature — Zombie Knight
 * 2/2
 *
 * Vanilla — no rules text.
 */
val HeadlessHorseman = card("Headless Horseman") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Knight"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Quinton Hoover"
        flavorText = "\". . . [T]he ghost rides forth to the scene of battle in nightly quest of his head . . . he sometimes passes along the Hollow, like a midnight blast . . .\"\n—Washington Irving, The Legend of Sleepy Hollow"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1aa37c8-98fa-4984-b09b-cf65ad84e97b.jpg?1783948066"
    }
}
