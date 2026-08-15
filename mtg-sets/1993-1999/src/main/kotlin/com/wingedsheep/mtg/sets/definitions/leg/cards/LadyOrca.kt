package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lady Orca
 * {5}{B}{R}
 * Legendary Creature — Demon
 * 7/4
 *
 * Vanilla — no rules text.
 */
val LadyOrca = card("Lady Orca") {
    manaCost = "{5}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Demon"
    power = 7
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "241"
        artist = "Sandra Everingham"
        flavorText = "\"I do not remember what he said to her. I remember her fiery eyes, fixed upon him for an instant. I remember a flash, and the hot breath of sudden flames made me turn away. When I looked again, Angus was gone.\" —A Wayfarer, on meeting Lady Orca"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2779553-74eb-42ba-97d0-96269f48c269.jpg?1783948036"
    }
}
