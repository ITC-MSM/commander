package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bonesplitter Sliver
 * {3}{R}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures get +2/+0.
 */
val BonesplitterSliver = card("Bonesplitter Sliver") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures get +2/+0."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Dany Orizio"
        flavorText = "As the time streams grew more and more unstable, Dominaria's creatures struggled to adapt. The intense pressures led to many dead ends but also to lethal new forms that appeared as suddenly as the ashen rains."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/705e29c5-2d9b-44ad-a04c-9a62dd74eb12.jpg"
    }
}
