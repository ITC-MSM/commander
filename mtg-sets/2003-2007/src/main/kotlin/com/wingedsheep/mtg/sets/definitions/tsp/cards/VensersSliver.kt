package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Venser's Sliver
 * {5}
 * Artifact Creature — Sliver
 * 3/3
 *
 * Vanilla — no rules text.
 */
val VensersSliver = card("Venser's Sliver") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Sliver"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "267"
        artist = "Carl Critchlow"
        flavorText = "Venser admired his handiwork and smiled. His first prototype had joined with the hive mind all too well, running with the brood and becoming a predator itself. This one, he thought, would be accepted into the hive but still obey his commands."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e3c5a64-453b-4477-853a-9514ba326f16.jpg?1783943196"
    }
}
