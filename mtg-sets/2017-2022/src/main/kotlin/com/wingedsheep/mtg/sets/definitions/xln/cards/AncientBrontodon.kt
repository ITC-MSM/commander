package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ancient Brontodon
 * {6}{G}{G}
 * Creature — Dinosaur
 * 9/9
 *
 * Vanilla — no rules text.
 */
val AncientBrontodon = card("Ancient Brontodon") {
    manaCost = "{6}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    power = 9
    toughness = 9

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Jakub Kasper"
        flavorText = "It is taller than all but the tallest trees, and older than all but the oldest."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39421ce8-86d5-4739-b6fd-78d63c0bb258.jpg?1783935731"
    }
}
