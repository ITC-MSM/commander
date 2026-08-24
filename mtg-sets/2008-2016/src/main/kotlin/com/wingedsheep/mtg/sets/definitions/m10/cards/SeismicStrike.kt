package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seismic Strike
 * {2}{R}
 * Instant
 * Seismic Strike deals damage to target creature equal to the number of Mountains you control.
 *
 * Canonical printing: Magic 2010, the card's earliest real-expansion printing. Reprinted in M15 as
 * a `Printing` row.
 */
val SeismicStrike = card("Seismic Strike") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Seismic Strike deals damage to target creature equal to the number of Mountains you control."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(DynamicAmounts.landsWithSubtype(Subtype.MOUNTAIN), t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Christopher Moeller"
        flavorText = "\"Life up here is simple. Adapt to the ways of the mountains and they will reward you. Fight them and they will end you.\"\n\u2014Kezim, prodigal pyromancer"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6526a501-9402-44c1-8e3c-ce01b4ed5b87.jpg?1783942369"
    }
}
