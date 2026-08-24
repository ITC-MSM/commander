package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Perish
 * {2}{B}
 * Sorcery
 * Destroy all green creatures. They can't be regenerated.
 */
val Perish = card("Perish") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy all green creatures. They can't be regenerated."

    spell {
        effect = Effects.DestroyAll(
            GameObjectFilter.Creature.withColor(Color.GREEN),
            noRegenerate = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Rebecca Guay"
        flavorText = "\"There will come a time when the voices of soil and seedling will sing only laments.\"\n" +
            "—Oracle *en*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e47ace1d-73de-44aa-a3fe-2e2a21ebec79.jpg"
    }
}
