package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Blanchwood Armor
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1 for each Forest you control.
 */
val BlanchwoodArmor = card("Blanchwood Armor") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 for each Forest you control."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.attachedCreature(),
            powerBonus = DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Land.withSubtype(Subtype.FOREST)
            ).count(),
            toughnessBonus = DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Land.withSubtype(Subtype.FOREST)
            ).count()
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b5f3776-74f4-4626-833b-e1b0921d3cbc.jpg?1782720658"
    }
}
