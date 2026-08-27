package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lost in a Labyrinth
 * {U}
 * Instant
 *
 * Target creature gets -3/-0 until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val LostInALabyrinth = card("Lost in a Labyrinth") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-0 until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(-3, 0, t),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Winona Nelson"
        flavorText = "Even those who leave the labyrinth never escape it, forever dreaming of their time trapped within."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09aa7744-680f-4c2a-8fa0-9cb0c176ae8f.jpg"
    }
}
