package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flowstone Infusion
 * {R}
 * Instant
 * Target creature gets +2/-2 until end of turn.
 */
val FlowstoneInfusion = card("Flowstone Infusion") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/-2 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, -2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Allen Williams"
        flavorText = "The rumor that contact with flowstone would reveal Phyrexian sleeper agents was spread mostly by Phyrexian sleeper agents."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b57c3674-9a31-4418-b306-e6b0a2514d8f.jpg?1783921318"
    }
}
