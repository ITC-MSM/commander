package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Might Sliver
 * {4}{G}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures get +2/+2.
 */
val MightSliver = card("Might Sliver") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures get +2/+2."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 2,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Jeff Miracola"
        flavorText = "\"The colossal thing rumbled over the ridge, tree husks crumbling before it. The ones we were already fighting howled as it came, their muscles suddenly surging, and we knew it was time to flee.\"\n—Llanach, Skyshroud ranger"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bb8c52f-e608-4710-872f-8a8ae2b5c00c.jpg"
    }
}
