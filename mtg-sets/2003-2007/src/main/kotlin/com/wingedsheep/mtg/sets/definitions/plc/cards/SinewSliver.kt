package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sinew Sliver
 * {1}{W}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures get +1/+1.
 *
 * The pump half of the Sliver lord shape. Sinew Sliver is itself a Sliver creature, so it pumps
 * itself — no `excludeSelf`, which is exactly what "all" means.
 */
val SinewSliver = card("Sinew Sliver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Steven Belledin"
        flavorText = "As the muscle cords of the creature twitched, Hanna saw an unsettling unanimity in the others' rippling flesh. She didn't know what it meant, but she urged Sisay to keep the ship at a safe distance."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6cd2ed50-cd9a-45d9-a59a-6279be1ab308.jpg"
    }
}
