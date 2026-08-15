package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Triton Shorethief
 * {U}
 * Creature — Merfolk Rogue
 * 1/2
 *
 * Vanilla — no rules text.
 */
val TritonShorethief = card("Triton Shorethief") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Howard Lyon"
        flavorText = "At sunrise, the Champion and her companions awoke to find their supplies gone and Brygus, their sentry, dead. Carefully arranged piles of ornamental shells gave a clear warning: go no further.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8f0fe22-dc89-4ad8-b5f6-6d91b61f1385.jpg?1783939787"
    }
}
