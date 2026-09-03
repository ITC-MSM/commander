package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ral's Outburst — War of the Spark #212 (canonical printing)
 * {2}{U}{R}
 * Instant
 * Ral's Outburst deals 3 damage to any target. Look at the top two cards of your library. Put
 * one of them into your hand and the other into your graveyard.
 *
 * Two sentences, two effects. The second is [Patterns.Library.lookAtTopAndKeep] at its plainest:
 * the keep destination and the rest destination are the recipe's own defaults (hand and
 * graveyard), which is also where its "Put in hand" / "Put in graveyard" choice labels come
 * from — so nothing beyond the two counts needs saying.
 */
val RalsOutburst = card("Ral's Outburst") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Ral's Outburst deals 3 damage to any target. Look at the top two cards of your " +
        "library. Put one of them into your hand and the other into your graveyard."

    spell {
        val victim = target("target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(3, victim),
            Patterns.Library.lookAtTopAndKeep(count = 2, keepCount = 1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "212"
        artist = "Joseph Meehan"
        flavorText = "\"Time to find the melting point of lazotep.\"\n—Ral Zarek"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6be3dd3e-50d2-4729-9caa-b2cd984f4c97.jpg"
    }
}
