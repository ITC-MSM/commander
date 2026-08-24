package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scour from Existence
 * {7}
 * Instant
 * Exile target permanent.
 */
val ScourFromExistence = card("Scour from Existence") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Instant"
    oracleText = "Exile target permanent."

    spell {
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Clint Cearley"
        flavorText = "\"Our people and our very lands disappear as if they never were. We no longer fight for " +
            "glory, or honor. We battle now for the right to exist.\"\n" +
            "—General Tazri, allied commander"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e47edb2-e43d-479f-a911-e72b67a06c3b.jpg?1783938222"
    }
}
