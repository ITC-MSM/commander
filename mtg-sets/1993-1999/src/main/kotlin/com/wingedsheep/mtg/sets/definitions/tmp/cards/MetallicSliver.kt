package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Metallic Sliver
 * {1}
 * Artifact Creature — Sliver
 * 1/1
 *
 * Vanilla — no rules text.
 */
val MetallicSliver = card("Metallic Sliver") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Sliver"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "297"
        artist = "Allen Williams"
        flavorText = "When the clever counterfeit was accepted by the hive, Volrath's influence upon the slivers grew even stronger."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30143f4f-9846-448d-8797-8fe0bc0cc5df.jpg?1783946602"
    }
}
