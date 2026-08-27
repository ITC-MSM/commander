package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shredding Winds
 * {2}{G}
 * Instant
 *
 * Shredding Winds deals 7 damage to target creature with flying.
 */
val ShreddingWinds = card("Shredding Winds") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Shredding Winds deals 7 damage to target creature with flying."

    spell {
        val t = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.DealDamage(7, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Christopher Moeller"
        flavorText = "\"Enemies of the wood! Your presence here is a slap in Nylea's face. Do not be surprised if she slaps back.\"\n—Telphe, druid of Nylea"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33f36143-85b6-412c-8c79-053728b45c25.jpg"
    }
}
