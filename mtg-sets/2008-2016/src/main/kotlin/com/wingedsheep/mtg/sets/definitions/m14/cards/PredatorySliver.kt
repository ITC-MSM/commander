package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Predatory Sliver
 * {1}{G}
 * Creature — Sliver
 * 1 / 1
 * Sliver creatures you control get +1/+1.
 */
val PredatorySliver = card("Predatory Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "Sliver creatures you control get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Mathias Kollros"
        flavorText = "No matter how much the slivers change, their collective might remains."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2e37de8-66a1-4afa-aa6f-1151f849dfa8.jpg"
    }
}
