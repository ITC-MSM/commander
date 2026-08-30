package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Megantic Sliver
 * {5}{G}
 * Creature — Sliver
 * 3 / 3
 * Sliver creatures you control get +3/+3.
 */
val MeganticSliver = card("Megantic Sliver") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 3
    toughness = 3
    oracleText = "Sliver creatures you control get +3/+3."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 3,
            toughnessBonus = 3,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Ryan Barger"
        flavorText = "Even the thrums, the lowliest of slivers, become deadly in its presence."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/7745f6a9-400c-4200-9732-86c54247de46.jpg"
    }
}
