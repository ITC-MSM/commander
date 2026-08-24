package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Take Heart
 * {W}
 * Instant
 * Target creature gets +2/+2 until end of turn. You gain 1 life for each attacking creature you control.
 */
val TakeHeart = card("Take Heart") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. You gain 1 life for each attacking creature you control."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, creature),
            Effects.GainLife(DynamicAmounts.attackingCreaturesYouControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Lucas Graciano"
        flavorText = "In the quiet before a battle, Boros soldiers whisper prayers that steady their nerves and focus their minds."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b26f10d5-b826-45b0-abed-80d86e1335c9.jpg?1783934194"
    }
}
