package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Yoked Ox
 * {W}
 * Creature — Ox
 * 0/4
 *
 * Vanilla — no rules text.
 */
val YokedOx = card("Yoked Ox") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Ox"
    power = 0
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Ryan Yee"
        flavorText = "It was in fields of grain, not fields of battle, that the Champion learned to bear the yoke of duty to the gods. She worked the land long before she was called on to defend it.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12fc09d4-e6ce-428b-818b-b465093af88e.jpg?1783939803"
    }
}
