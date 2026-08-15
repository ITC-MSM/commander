package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vastwood Gorger
 * {5}{G}
 * Creature — Wurm
 * 5/6
 *
 * Vanilla — no rules text.
 */
val VastwoodGorger = card("Vastwood Gorger") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 5
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Kieran Yanner"
        flavorText = "\"I've known true ferocity and power. Those brazen 'planar-walkers' have no idea what wild really means.\"\n—Chadir the Navigator"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc5daf96-ceae-4c9a-95cd-f6d706e9b1fa.jpg?1783942129"
    }
}
