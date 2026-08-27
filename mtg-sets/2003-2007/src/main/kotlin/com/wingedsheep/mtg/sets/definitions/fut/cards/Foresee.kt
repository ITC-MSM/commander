package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Foresee
 * {3}{U}
 * Sorcery
 *
 * Scry 4, then draw two cards.
 *
 * "then" is sequencing, not a condition — a plain [Effects.Composite] of the compact
 * [Effects.Scry] macro and the draw. The engine expands the scry macro into its
 * look/bottom/top pipeline at resolution.
 */
val Foresee = card("Foresee") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Scry 4, then draw two cards."

    spell {
        effect = Effects.Composite(
            Effects.Scry(4),
            Effects.DrawCards(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Ron Spears"
        flavorText = "Wind time's watch, and watch time unwind."
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0bd95b5-1e90-41c5-8a9f-f3009f3f504a.jpg?1783943122"
    }
}
