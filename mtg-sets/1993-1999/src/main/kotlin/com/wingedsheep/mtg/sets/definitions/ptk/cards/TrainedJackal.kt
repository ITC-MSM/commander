package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trained Jackal
 * {G}
 * Creature — Jackal
 * 1/2
 *
 * Vanilla — no rules text.
 */
val TrainedJackal = card("Trained Jackal") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Jackal"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Yang Jun Kwon"
        flavorText = "To taunt a man into action, call him a coward. To insult him beyond forgiveness, call him a jackal."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01deb3cc-91e8-4ef3-964f-f36c6a21207c.jpg?1783946096"
    }
}
