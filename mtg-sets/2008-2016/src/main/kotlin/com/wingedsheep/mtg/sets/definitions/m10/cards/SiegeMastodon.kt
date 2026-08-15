package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Siege Mastodon
 * {4}{W}
 * Creature — Elephant
 * 3/5
 *
 * Vanilla — no rules text.
 */
val SiegeMastodon = card("Siege Mastodon") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant"
    power = 3
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Matt Cavotta"
        flavorText = "\"Defending the citadel is easy. I just take these big fellows outside the main gate and let the enemy take a good long look.\"\n—Mastodon keeper"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/287d4c56-1b75-4ac4-8be8-333b1aba982a.jpg?1783942399"
    }
}
