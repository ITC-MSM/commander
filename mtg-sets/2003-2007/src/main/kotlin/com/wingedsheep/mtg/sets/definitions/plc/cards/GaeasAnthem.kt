package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gaea's Anthem
 * {1}{G}{G}
 * Enchantment
 * Creatures you control get +1/+1.
 */
val GaeasAnthem = card("Gaea's Anthem") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Creatures you control get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "147"
        artist = "Greg Staples"
        flavorText = "\"To those who can hear it, Gaea's battle song brings power as swift as sunlight and as enduring as the deep roots of the forest.\"\n—Gamelen, Citanul elder"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea9e0de1-2299-4ff9-b49a-88535a96bda0.jpg"
    }
}
