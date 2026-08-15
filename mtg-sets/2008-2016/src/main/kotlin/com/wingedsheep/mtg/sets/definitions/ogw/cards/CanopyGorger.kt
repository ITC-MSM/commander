package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Canopy Gorger
 * {4}{G}{G}
 * Creature — Wurm
 * 6/5
 *
 * Vanilla — no rules text.
 */
val CanopyGorger = card("Canopy Gorger") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 6
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Lake Hurwitz"
        flavorText = "The settlement's defenders were glad to have such a massive, ferocious creature join the fight—but less glad to see it flatten their homes along the way."
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbc8957d-769c-4630-9544-56cea8c847c2.jpg?1783937902"
    }
}
