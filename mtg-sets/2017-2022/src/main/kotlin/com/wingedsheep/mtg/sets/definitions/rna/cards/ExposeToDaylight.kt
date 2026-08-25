package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Expose to Daylight — Ravnica Allegiance #8
 * {2}{W} · Instant
 *
 * [Targets.ArtifactOrEnchantment] is the shared disjunction rather than a hand-rolled `Or`,
 * followed by the same scry rider the RNA removal commons share.
 */
val ExposeToDaylight = card("Expose to Daylight") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment. Scry 1."

    spell {
        val permanent = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Composite(listOf(
            Effects.Destroy(permanent),
            Effects.Scry(1)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Daniel Ljunggren"
        flavorText = "\"Lies cannot long withstand the harsh light of day.\"\n" +
        "—Lavinia"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/094c2ac3-040f-41fe-9a37-c037d90baec0.jpg"
    }
}
