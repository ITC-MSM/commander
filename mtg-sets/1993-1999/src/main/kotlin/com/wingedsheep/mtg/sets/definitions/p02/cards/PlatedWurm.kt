package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Plated Wurm
 * {4}{G}
 * Creature — Wurm
 * 4/5
 *
 * Vanilla — no rules text.
 */
val PlatedWurm = card("Plated Wurm") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Daniel Gelon"
        flavorText = "\"I'd hate to see the bird that could get *this* wurm!\"\n—Alaborn soldier"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b51f8724-8f26-4a9d-b586-4223354ae7fc.jpg?1783946449"
    }
}
