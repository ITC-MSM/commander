package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Traveling Philosopher
 * {1}{W}
 * Creature — Human Advisor
 * 2/2
 *
 * Vanilla — no rules text.
 */
val TravelingPhilosopher = card("Traveling Philosopher") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Advisor"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "James Ryman"
        flavorText = "The Champion and the philosopher Olexa returned from the opposing camp at dusk. Behind them, the enemy raised sail and departed, breaking the siege. When asked what the two had done, the Champion replied, \"We spoke to them.\"\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edad0276-45d1-45e5-a6b1-1cd2a99b4f2c.jpg?1783939808"
    }
}
