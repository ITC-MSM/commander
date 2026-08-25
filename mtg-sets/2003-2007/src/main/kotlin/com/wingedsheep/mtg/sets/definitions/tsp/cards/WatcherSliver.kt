package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Watcher Sliver
 * {3}{W}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures get +0/+2.
 */
val WatcherSliver = card("Watcher Sliver") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures get +0/+2."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 2,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Liz Danforth"
        flavorText = "\"I have spied them, wandering and watching—but for what? I fear they watch for us.\"\n—Yonat of Amrou"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d72a950-82ed-4e7b-9e18-b8231a2ebea7.jpg"
    }
}
