package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Imperial Oath — Kamigawa: Neon Dynasty #17 (canonical printing)
 * {5}{W} · Sorcery
 *
 * Create three 2/2 white Samurai creature tokens with vigilance. Scry 3.
 */
val ImperialOath = card("Imperial Oath") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create three 2/2 white Samurai creature tokens with vigilance. Scry 3."

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Samurai"),
            keywords = setOf(Keyword.VIGILANCE),
            count = 3,
        ) then Patterns.Library.scry(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Nicholas Elias"
        flavorText = "Those chosen to guard the Imperial Palace are not only unrivaled warriors " +
            "but also lionhearted champions of the Imperial cause."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d6750dd-2303-493b-885d-1bfb5787b16c.jpg?1783923922"
    }
}
