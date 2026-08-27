package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Glare of Heresy
 * {1}{W}
 * Sorcery
 *
 * Exile target white permanent.
 *
 * "White permanent" is the permanent filter plus a colour predicate, so it reads *projected* colour
 * — a permanent turned white by a continuous effect is a legal target, and a white one turned
 * colourless is not.
 */
val GlareOfHeresy = card("Glare of Heresy") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target white permanent."

    spell {
        val permanent = target(
            "white permanent",
            TargetPermanent(filter = TargetFilter.Permanent.withColor(Color.WHITE))
        )
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Raymond Swanland"
        flavorText = "No foe is more hated than the former friend."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ed3d16f-f0c6-4080-8913-758208b08234.jpg"
    }
}
