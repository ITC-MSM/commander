package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nema Siltlurker
 * {4}{G}
 * Creature — Lizard
 * 3/5
 *
 * Vanilla — no rules text.
 */
val NemaSiltlurker = card("Nema Siltlurker") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lizard"
    power = 3
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "200"
        artist = "Wayne Reynolds"
        flavorText = "In one gargantuan bite, it will swallow not only you but also the riverbank you were standing on."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a477e081-949f-4cf0-b0d2-b9bdff6c760d.jpg?1783941961"
    }
}
