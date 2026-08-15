package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Strength of Unity
 * {3}{W}
 * Enchantment — Aura
 * Enchant creature
 * Domain — Enchanted creature gets +1/+1 for each basic land type among lands you control.
 */
val StrengthOfUnity = card("Strength of Unity") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Domain — Enchanted creature gets +1/+1 for each basic land type among lands you control."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = DynamicAmounts.domain(),
            toughnessBonus = DynamicAmounts.domain()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Andrew Goldhawk"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a9d4ff8-af35-413f-9aa2-f4c6e34fade2.jpg?1562900233"
    }
}
