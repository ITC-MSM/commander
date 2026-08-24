package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Muscle Sliver
 * {1}{G}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures get +1/+1.
 */
val MuscleSliver = card("Muscle Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Richard Kane Ferguson"
        flavorText = "The air was filled with the cracks and snaps of flesh hardening as the new sliver joined the battle."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/602a1e1f-4195-48c0-8290-562e7e0db6d8.jpg"
    }
}
