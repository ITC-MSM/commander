package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silverstrike (Shadows over Innistrad #37)
 * {3}{W}
 * Instant
 *
 * Destroy target attacking creature. You gain 3 life.
 */
val Silverstrike = card("Silverstrike") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target attacking creature. You gain 3 life."

    spell {
        val victim = target("target", Targets.AttackingCreature)
        effect = Effects.Composite(
            Effects.Destroy(victim),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Lius Lasahido"
        flavorText = "\"Shield yourself with faith. Arm yourself with silver.\"\n—Slayer Kastinne"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f27b92a-cde9-41bc-9b23-d83b74b167d4.jpg?1783937812"
    }
}
