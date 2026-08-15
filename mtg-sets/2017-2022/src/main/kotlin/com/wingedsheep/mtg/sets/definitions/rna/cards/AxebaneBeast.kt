package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Axebane Beast
 * {3}{G}
 * Creature — Beast
 * 3/4
 *
 * Vanilla — no rules text.
 */
val AxebaneBeast = card("Axebane Beast") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "Sam Rowan"
        flavorText = "\"Imagine a gigantic pine cone that's extremely territorial and always in a foul mood.\"\n—Zhosmir, urban huntmaster"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f420b35-1f73-41c8-a15f-1aee4af0999c.jpg?1783933673"
    }
}
