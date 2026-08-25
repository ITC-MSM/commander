package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Cleaving Sliver
 * {3}{R}
 * Creature — Sliver
 * 2/2
 * Sliver creatures you control get +2/+0.
 *
 * A plain Sliver lord: [ModifyStats] over every Sliver creature you control. The printed noun is
 * "Sliver **creatures**", so the filter is [GameObjectFilter.Creature] with the subtype rather than
 * the bare-tribal-noun `Permanent` form, and the lord counts itself (no `excludeSelf`).
 */
val CleavingSliver = card("Cleaving Sliver") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control get +2/+0."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "David Gaillet"
        flavorText = "When it wriggled closer to a thrum of slivers, their talons hardened and glinted in the sun. One took a playful swipe at a tree trunk, and its new blade cut clean through."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51b657f0-6636-4d8a-9176-81027816e0ec.jpg?1783933115"
    }
}
