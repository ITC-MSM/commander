package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Scarwood Treefolk
 * {3}{G}
 * Creature — Treefolk
 * 3 / 5
 * This creature enters tapped.
 */
val ScarwoodTreefolk = card("Scarwood Treefolk") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk"
    power = 3
    toughness = 5
    oracleText = "This creature enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "214"
        artist = "Stuart Griffin"
        flavorText = "To treefolk's sense of time, ages pass as hours. They stood as witnesses to the apocalypse, the years of which they saw as one cacophonous, ultra-destructive moment."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed6dece3-058c-4738-a22f-8893345ddd1c.jpg"
    }
}
