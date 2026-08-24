package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flesh to Dust
 * {3}{B}{B}
 * Instant
 * Destroy target creature. It can't be regenerated.
 */
val FleshToDust = card("Flesh to Dust") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature. It can't be regenerated."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Destroy(t, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Julie Dillon"
        flavorText = "\"Pain is temporary. So is life.\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16b2e842-6c92-47b0-bed4-e0e64485f168.jpg?1783939183"
    }
}
