package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rotted Hulk
 * {3}{B}
 * Creature — Elemental
 * 2/5
 *
 * Vanilla — no rules text.
 */
val RottedHulk = card("Rotted Hulk") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elemental"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Raymond Swanland"
        flavorText = "The hulk rose from the sea and loomed over the Champion. Pinned beneath the twisting, rotted planks of wood was the body of Kaliaros, the helmsman of her former crew, and beside him the captain, Photine.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1066644a-ac62-4809-805c-607c645613c5.jpg?1783939431"
    }
}
