package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Crucible of Fire
 * {3}{R}
 * Enchantment
 * Dragon creatures you control get +3/+3.
 *
 * Canonical printing: Shards of Alara, the card's earliest real-expansion printing. Reprinted in
 * M15 as a `Printing` row.
 */
val CrucibleOfFire = card("Crucible of Fire") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Dragon creatures you control get +3/+3."

    staticAbility {
        ability = ModifyStats(
            +3, +3,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.DRAGON).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "96"
        artist = "Dominick Domingo"
        flavorText = "\"The dragon is a perfect marriage of power and the will to use it.\"\n—Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/38a2d4ba-7bd0-4852-aad3-dfdaf5368e3e.jpg?1783942563"
    }
}
