package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Holy Day
 * {W}
 * Instant
 * Prevent all combat damage that would be dealt this turn.
 *
 * Legends is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here. Later sets (Invasion, etc.)
 * contribute reprint [com.wingedsheep.sdk.model.Printing] rows.
 */
val HolyDay = card("Holy Day") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn."

    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Justin Hampton"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6c95a2b-bf44-4ff2-9c6a-916773346edd.jpg?1591104919"
    }
}
