package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seize the Initiative
 * {W}
 * Instant
 *
 * Target creature gets +1/+1 and gains first strike until end of turn.
 */
val SeizeTheInitiative = card("Seize the Initiative") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+1 and gains first strike until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, t),
            Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Steve Argyle"
        flavorText = "The time between spotting a leonin shikari and feeling its claws is just time enough to draw your last breath."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d745f35-944a-4157-a351-baa06f67b725.jpg?1783941743"
    }
}
