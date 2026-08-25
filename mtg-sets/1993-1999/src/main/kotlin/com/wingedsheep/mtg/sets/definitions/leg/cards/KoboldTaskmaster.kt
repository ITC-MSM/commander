package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Kobold Taskmaster
 * {1}{R}
 * Creature — Kobold
 * 1/2
 *
 * Other Kobold creatures you control get +1/+0.
 */
val KoboldTaskmaster = card("Kobold Taskmaster") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kobold"
    power = 1
    toughness = 2
    oracleText = "Other Kobold creatures you control get +1/+0."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.KOBOLD).youControl(),
                excludeSelf = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "156"
        artist = "Randy Asplund-Faith"
        flavorText = "The Taskmaster knows that there is no cure for the common Kobold."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b9c63eb-8d4e-4d8b-8637-308459ef036b.jpg?1783948054"
    }
}
