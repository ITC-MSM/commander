package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Temporal Eddy
 * {2}{U}{U}
 * Sorcery
 *
 * Put target creature or land on top of its owner's library.
 */
val TemporalEddy = card("Temporal Eddy") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Put target creature or land on top of its owner's library."

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.CreatureOrLandPermanent))
        effect = Effects.PutOnTopOfLibrary(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Wayne England"
        flavorText = "As the temporal fractures spread and time itself slowly fell apart, visitors started to appear from across the past and future, and those native to the present began to disappear."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cab2147-d496-489b-aaf0-b354e31a6b45.jpg"
    }
}
