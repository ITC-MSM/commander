package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Neck Snap
 * {3}{W}
 * Instant
 * Destroy target attacking or blocking creature.
 */
val NeckSnap = card("Neck Snap") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target attacking or blocking creature."

    spell {
        val creature = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Dominick Domingo"
        flavorText = "\"We merrows need not be disadvantaged when fighting on land. We lack the vulnerability of those who breathe only through their throats.\"\n—Minnarin, merrow reejerey"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc326b79-363e-4c14-86e4-23041f2d6b4f.jpg?1783942911"
    }
}
