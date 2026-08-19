package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Fissure
 * {3}{R}{R}
 * Instant
 * Destroy target creature or land. It can't be regenerated.
 */
val Fissure = card("Fissure") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Destroy target creature or land. It can't be regenerated."

    spell {
        val permanent = target(
            "target creature or land",
            TargetPermanent(filter = TargetFilter.CreatureOrLandPermanent)
        )
        effect = Effects.Destroy(permanent, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Douglas Shuler"
        flavorText = "\"Must not all things at the last be swallowed up in death?\" —Plato"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa2d778d-d74b-45ec-a86b-5d52ffad6ba5.jpg?1783947935"
    }
}
