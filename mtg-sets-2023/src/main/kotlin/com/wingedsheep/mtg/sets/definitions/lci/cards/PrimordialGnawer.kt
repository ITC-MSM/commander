package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Primordial Gnawer
 * {4}{B}
 * Creature — Insect Horror
 * 5/2
 * When this creature dies, discover 3.
 */
val PrimordialGnawer = card("Primordial Gnawer") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Horror"
    power = 5
    toughness = 2
    oracleText = "When this creature dies, discover 3."
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Discover(3)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a18f5ad0-e9c1-4e45-b245-2946e29baecb.jpg?1782694519"
    }
}
