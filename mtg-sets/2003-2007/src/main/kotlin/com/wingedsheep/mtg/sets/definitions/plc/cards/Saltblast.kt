package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Saltblast
 * {3}{W}{W}
 * Sorcery
 * Destroy target nonwhite permanent.
 *
 * "Nonwhite" is `CardPredicate.NotColor(WHITE)`, which reads the permanent's colours — a
 * multicoloured permanent that is partly white is spared.
 */
val Saltblast = card("Saltblast") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target nonwhite permanent."

    spell {
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Permanent.notColor(Color.WHITE)))
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Paolo Parente"
        flavorText = "Dominaria erodes with each passing gust."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edd1833d-64b0-4c9b-8f6b-1cf15c29d473.jpg"
    }
}
