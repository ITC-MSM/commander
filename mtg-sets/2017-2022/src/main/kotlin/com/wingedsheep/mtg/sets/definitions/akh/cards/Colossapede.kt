package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Colossapede
 * {4}{G}
 * Creature — Insect
 * 5/5
 *
 * Vanilla — no rules text.
 */
val Colossapede = card("Colossapede") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Jason Kang"
        flavorText = "\"If it is bigger, you must be faster. If it is stronger, you must be sharper. Anything less, and you will never seize a place in our God-Pharaoh's perfect afterlife.\"\n—Rhonas, god of strength"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7642bfc5-ace8-419e-b57b-2b881bfe023e.jpg?1783936478"
    }
}
