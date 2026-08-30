package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wring Flesh
 * {B}
 * Instant
 * Target creature gets -3/-1 until end of turn.
 *
 * Canonical printing: Magic 2012, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 */
val WringFlesh = card("Wring Flesh") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-1 until end of turn."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-3, -1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Izzy"
        flavorText = "\"Don't blame me. You're the one walking around with skin.\"\n" +
            "—Zul Ashur, lich lord"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/663df3e8-12e5-46cf-9da7-39961feaa7f9.jpg"
    }
}
