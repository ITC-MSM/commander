package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Show of Valor
 * {1}{W}
 * Instant
 * Target creature gets +2/+4 until end of turn.
 *
 * Canonical printing: Magic 2013, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 */
val ShowOfValor = card("Show of Valor") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+4 until end of turn."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 4, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Anthony Palumbo"
        flavorText = "\"Duty, honor, and valor are either in your heart or they are not. You will never know for certain until you are tested.\"\n" +
            "—Ajani Goldmane"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abe4d19d-1c9f-4b05-bde2-a9290b52c28d.jpg"
    }
}
