package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sliver Construct
 * {3}
 * Artifact Creature — Sliver Construct
 * 2/2
 *
 * Vanilla — no rules text.
 */
val SliverConstruct = card("Sliver Construct") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Sliver Construct"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Mathias Kollros"
        flavorText = "Slivers destroy those who come close to the Skep, the central hive. Shards of torn metal litter the ground as a warning to any artificers inquisitive about the hive's inner workings."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/3129645a-221c-4eb5-88fd-12cc742a1dfe.jpg?1783939893"
    }
}
