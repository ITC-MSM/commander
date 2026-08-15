package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fomori Nomad
 * {4}{R}
 * Creature — Nomad Giant
 * 4/4
 *
 * Vanilla — no rules text.
 */
val FomoriNomad = card("Fomori Nomad") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Nomad Giant"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/8/2/8216b3f3-b9b6-4d90-a624-c1b9b7bdf953.jpg?1783943103"
    }
}
