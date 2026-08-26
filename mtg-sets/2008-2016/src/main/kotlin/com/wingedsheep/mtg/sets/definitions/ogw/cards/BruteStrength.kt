package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brute Strength
 * {1}{R}
 * Instant
 * Target creature gets +3/+1 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val BruteStrength = card("Brute Strength") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 1, creature),
            Effects.GrantKeyword(Keyword.TRAMPLE, creature)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Wayne Reynolds"
        flavorText = "It's not the size of the rock. It's how badly you want to lift it."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec60192a-19b3-447c-b732-bbcb2d275df6.jpg?1783937908"
    }
}
