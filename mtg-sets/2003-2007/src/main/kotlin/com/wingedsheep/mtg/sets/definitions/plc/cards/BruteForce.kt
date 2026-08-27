package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brute Force
 * {R}
 * Instant
 * Target creature gets +3/+3 until end of turn.
 */
val BruteForce = card("Brute Force") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Wayne Reynolds"
        flavorText = "Blood, bone, and sinew are magnified, as is the rage that drives them. The brain, however, remains unchanged—a little bean, swinging by a strand in a cavernous, raving head."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82d43220-1e4e-4b61-9844-51c8bb5dde35.jpg"
    }
}
