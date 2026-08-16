package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Yotian Tactician
 * {2}{W}{U}
 * Creature — Human Soldier
 * 3/4
 * Other Soldiers you control get +1/+1.
 *
 * The bare tribal noun ("Other Soldiers you control") means *permanents* with that subtype, not
 * creatures — so the group filter is [GameObjectFilter.Permanent]`.withSubtype(Soldier)`, with
 * `excludeSelf` carrying the "Other".
 */
val YotianTactician = card("Yotian Tactician") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 4
    oracleText = "Other Soldiers you control get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.SOLDIER).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Fariba Khamseh"
        flavorText = "\"Time for the Iron Alliance to remind the qadir's army how inhospitable the Sword Marches can be.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/6/760c0369-c2e4-4bd7-a301-9f707f5f48a8.jpg?1783920023"
    }
}
