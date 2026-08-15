package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gilded Sentinel
 * {4}
 * Artifact Creature — Golem
 * 3/3
 *
 * Vanilla — no rules text.
 */
val GildedSentinel = card("Gilded Sentinel") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "239"
        artist = "Izzy"
        flavorText = "The River Heralds fight to keep all others from reaching the golden city. The city has its own defenses."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01cc1a59-76bf-4721-b4a7-ef746f3d3990.jpg?1783935702"
    }
}
