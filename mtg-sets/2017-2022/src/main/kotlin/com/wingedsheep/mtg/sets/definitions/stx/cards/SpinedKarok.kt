package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spined Karok
 * {2}{G}
 * Creature — Crocodile
 * 2/4
 *
 * Vanilla — no rules text.
 */
val SpinedKarok = card("Spined Karok") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Crocodile"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Filip Burburan"
        flavorText = "\"The bogs are deeper than you realize. Most of the space beneath the surface is taken up by these enormous karoks. You might think your feet are touching the bottom. They're probably not.\"\n—Gyome, Witherbloom chef"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c37ae6b5-225a-410e-ab22-13e923bdfb65.jpg?1783927338"
    }
}
