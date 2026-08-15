package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Kavu Scout
 * {2}{R}
 * Creature — Kavu Scout
 * 0/2
 * Domain — This creature gets +1/+0 for each basic land type among lands you control.
 */
val KavuScout = card("Kavu Scout") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu Scout"
    power = 0
    toughness = 2
    oracleText = "Domain — This creature gets +1/+0 for each basic land type among lands you control."

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmounts.domain(),
            toughnessBonus = DynamicAmount.Fixed(0)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "DiTerlizzi"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbc2670d-a3f4-47c2-b424-01fd379ff186.jpg?1562935954"
    }
}
