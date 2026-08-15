package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Razorfield Thresher
 * {7}
 * Artifact Creature — Construct
 * 6/4
 *
 * Vanilla — no rules text.
 */
val RazorfieldThresher = card("Razorfield Thresher") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 6
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Karl Kopinski"
        flavorText = "DANGER! Keep appendages clear of front of machine. And rear of machine. And side of machine. And top of machine."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0a74203-d342-489d-a584-bca78ef3331d.jpg?1783941698"
    }
}
