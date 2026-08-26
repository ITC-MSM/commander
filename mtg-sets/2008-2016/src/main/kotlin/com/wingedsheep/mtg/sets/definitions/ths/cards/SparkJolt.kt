package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spark Jolt
 * {R}
 * Instant
 *
 * Spark Jolt deals 1 damage to any target. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val SparkJolt = card("Spark Jolt") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Spark Jolt deals 1 damage to any target. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(1, t),
            Effects.Scry(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Mike Bierek"
        flavorText = "Acolytes of Purphoros hammer the world until they see the sparks of change."
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ee479c2-a115-450b-bc2e-b03d23b82f2d.jpg"
    }
}
