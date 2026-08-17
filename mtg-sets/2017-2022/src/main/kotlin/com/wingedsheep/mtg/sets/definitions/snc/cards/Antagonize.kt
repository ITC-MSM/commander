package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Antagonize
 * {1}{R}
 * Instant
 * Target creature gets +4/+3 until end of turn.
 */
val Antagonize = card("Antagonize") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +4/+3 until end of turn."

    spell {
        val creature = target("target creature to get +4/+3", TargetCreature())
        effect = Effects.ModifyStats(4, 3, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Gabor Szikszai"
        flavorText = "\"The little guy had guts at least. You can see 'em right there between those cobbles.\"\n—Glunk, freelance crusher"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bac1d2a-99dd-40b3-8823-ce9225efcdcf.jpg?1783923123"
    }
}
