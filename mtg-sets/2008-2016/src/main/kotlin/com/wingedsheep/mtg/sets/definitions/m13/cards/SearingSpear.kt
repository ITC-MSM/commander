package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Searing Spear
 * {1}{R}
 * Instant
 * Searing Spear deals 3 damage to any target.
 */
val SearingSpear = card("Searing Spear") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Searing Spear deals 3 damage to any target."

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Chris Rahn"
        flavorText = "Sometimes you die a glorious death with your sword held high. Sometimes you're just target practice."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11a94b7c-0216-473c-87a6-71e5a64d7799.jpg?1783940479"
    }
}
