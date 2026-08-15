package com.wingedsheep.mtg.sets.definitions.mmq.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fresh Volunteers
 * {1}{W}
 * Creature — Human Rebel
 * 2/2
 *
 * Vanilla — no rules text.
 */
val FreshVolunteers = card("Fresh Volunteers") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Rebel"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Jeff Miracola"
        flavorText = "Every Cho-Arrim villager is a potential warrior; when they are called, they abandon their peaceful way of life and take up arms to defend it."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e070ea4a-c417-405f-b788-78fb7ca2eaa5.jpg?1783945981"
    }
}
