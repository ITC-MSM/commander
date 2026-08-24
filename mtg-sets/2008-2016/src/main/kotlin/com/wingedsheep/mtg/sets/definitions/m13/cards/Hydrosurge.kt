package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hydrosurge
 * {U}
 * Instant
 * Target creature gets -5/-0 until end of turn.
 *
 * Canonical printing: Magic 2013, the card's earliest real-expansion printing. Reprinted in M15 as
 * a `Printing` row.
 */
val Hydrosurge = card("Hydrosurge") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -5/-0 until end of turn."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-5, 0, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Steve Prescott"
        flavorText = "\"Thirsty?\"\n—Drunvalus, hydromancer"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a22f992-ef16-45be-8bac-bd7418ed068f.jpg?1783940507"
    }
}
