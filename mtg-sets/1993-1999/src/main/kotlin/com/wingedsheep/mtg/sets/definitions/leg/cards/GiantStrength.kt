package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Giant Strength
 * {R}{R}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2.
 */
val GiantStrength = card("Giant Strength") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2."
    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Justin Hampton"
        flavorText = "\"O! it is excellent/ To have a giant's strength, but it is tyrannous/ " +
            "To use it like a giant.\" —William Shakespeare, *Measure for Measure*"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a86190bb-1f41-4128-b9fb-dfb1d178359d.jpg"
    }
}
