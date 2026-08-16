package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * King of the Pride
 * {2}{W}
 * Creature — Cat
 * 2/1
 * Other Cats you control get +2/+1.
 *
 * A plain lord: [ModifyStats] over every Cat you control, `excludeSelf = true` for "other".
 */
val KingOfThePride = card("King of the Pride") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 1
    oracleText = "Other Cats you control get +2/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.CAT).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "16"
        artist = "Jonathan Kuo"
        flavorText = "\"Glorious, to walk again across the savannah with my beloved.\" —\"Love Song of Night and Day\""
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4e83abd-6f15-491e-9253-90af9f6f1025.jpg?1783933160"
        ruling(
            "2024-11-08",
            "Because damage remains marked on a creature until the damage is removed as the turn " +
                "ends, nonlethal damage dealt to a Cat you control may become lethal if King of " +
                "the Pride leaves the battlefield during that turn."
        )
    }
}
