package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Golgari Longlegs
 * {3}{B/G}{B/G}
 * Creature — Insect
 * 5/4
 *
 * Vanilla — no rules text.
 */
val GolgariLonglegs = card("Golgari Longlegs") {
    manaCost = "{3}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Creature — Insect"
    power = 5
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Volkan Baǵa"
        flavorText = "Despite its enormous stature, it can fold itself into a tunnel with startling quickness, vanishing back into the undercity."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d44058ba-3419-4777-8d59-05dea5e864e1.jpg?1783940327"
    }
}
