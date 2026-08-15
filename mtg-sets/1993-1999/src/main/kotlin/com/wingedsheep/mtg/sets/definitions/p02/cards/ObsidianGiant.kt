package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Obsidian Giant
 * {4}{R}
 * Creature — Giant
 * 4/4
 *
 * Vanilla — no rules text.
 */
val ObsidianGiant = card("Obsidian Giant") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "David A. Cherry"
        flavorText = "After a while, ships stopped sailing within his reach."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aad8a194-cee7-4671-8310-19357fc1a450.jpg?1783946462"
    }
}
