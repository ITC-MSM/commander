package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Loxodon Line Breaker
 * {2}{W}
 * Creature — Elephant Soldier
 * 3/2
 *
 * Vanilla — no rules text.
 */
val LoxodonLineBreaker = card("Loxodon Line Breaker") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Soldier"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Jesper Ejsing"
        flavorText = "Loxodons are firm in stature and spirit. No matter the odds, they are always first into battle."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/928d4250-c379-4134-a263-7811c80a8760.jpg?1783934603"
    }
}
