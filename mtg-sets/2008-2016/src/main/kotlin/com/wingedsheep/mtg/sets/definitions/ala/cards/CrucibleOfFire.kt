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
 * A layer-7c lord as a single [ModifyStats] static ability. The enchantment is not itself a Dragon,
 * so the printed line has no "other" and the [GroupFilter] keeps its default `excludeSelf = false`;
 * the filter is read against projected state, so a creature that only becomes a Dragon later is
 * picked up on the next projection.
 */
val CrucibleOfFire = card("Crucible of Fire") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Dragon creatures you control get +3/+3."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 3,
            toughnessBonus = 3,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.DRAGON).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "96"
        artist = "Dominick Domingo"
        flavorText = "\"The dragon is a perfect marriage of power and the will to use it.\"\n—Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/38a2d4ba-7bd0-4852-aad3-dfdaf5368e3e.jpg"
    }
}
