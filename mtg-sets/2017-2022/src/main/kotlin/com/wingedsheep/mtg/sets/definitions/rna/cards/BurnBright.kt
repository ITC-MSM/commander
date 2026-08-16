package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Burn Bright
 * {2}{R}
 * Instant
 * Creatures you control get +2/+0 until end of turn.
 */
val BurnBright = card("Burn Bright") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Creatures you control get +2/+0 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(2, 0, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Scott Murphy"
        flavorText = "\"From a great bonfire at the dawn of time, the first Gruul kindled their rage. The same flame burns in you.\" —Kroshkar, Gruul shaman"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8574ffd-3e72-41de-90bf-69363189f047.jpg?1783933685"
    }
}
