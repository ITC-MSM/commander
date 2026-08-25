package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tempered Steel — Scars of Mirrodin #24
 * {1}{W}{W} · Enchantment
 *
 * Artifact creatures you control get +2/+2.
 *
 * A plain layer-7c (CR 613.4c) [ModifyStats] lord over a group filter, recomputed at projection —
 * so an artifact creature that enters, changes controller, or stops being an artifact picks the
 * bonus up or drops it immediately, with no trigger and no timestamp of its own.
 */
val TemperedSteel = card("Tempered Steel") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Artifact creatures you control get +2/+2."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 2,
            filter = GroupFilter(GameObjectFilter.ArtifactCreature.youControl()),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Wayne Reynolds"
        flavorText = "\"Death shall prevail as long as our will falls to rust. May necessity anneal our resolve.\"\n—Ghalma the Shaper"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/6661b39d-505a-48f4-bc06-59084c6a3b0c.jpg?1783941742"
    }
}
