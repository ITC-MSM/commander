package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Clear a Path
 * {R}
 * Sorcery
 * Destroy target creature with defender.
 *
 * Canonical printing: Dragon's Maze, the card's earliest real-expansion printing. Reprinted in M15
 * as a `Printing` row.
 */
val ClearAPath = card("Clear a Path") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature with defender."

    spell {
        val t = target(
            "target creature with defender",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER)))
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Karl Kopinski"
        flavorText = "\"Why do guards always look surprised when we bash them?\" asked Ruric. \"I think they expect a bribe,\" said Thar."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a8f904b-a9a3-4bae-9284-4e9cbe7592ee.jpg?1783940038"
    }
}
