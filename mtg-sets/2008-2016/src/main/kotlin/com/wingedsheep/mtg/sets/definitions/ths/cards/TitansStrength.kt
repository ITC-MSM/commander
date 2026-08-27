package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Titan's Strength
 * {R}
 * Instant
 *
 * Target creature gets +3/+1 until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val TitansStrength = card("Titan's Strength") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 1, t),
            Effects.Scry(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Karl Kopinski"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3db6ee53-c5e6-4344-9f35-084cd8cc9cd3.jpg"
    }
}
