package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Rampaging Soulrager
 * {2}{R}
 * Creature — Spirit
 * 1/4
 *
 * This creature gets +3/+0 as long as there are two or more unlocked doors among Rooms you control.
 *
 * A "Rooms-matter" payoff: the +3/+0 is a [ConditionalStaticAbility] gated on
 * [Conditions.UnlockedDoorsAtLeast] (CR 709.5), which counts each unlocked door face among Rooms
 * the controller has — a Room with both doors unlocked counts as two.
 */
val RampagingSoulrager = card("Rampaging Soulrager") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 4
    oracleText = "This creature gets +3/+0 as long as there are two or more unlocked doors among Rooms you control."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 3, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.UnlockedDoorsAtLeast(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/569c914b-95af-428f-a142-6f20e418bc59.jpg?1726286414"
    }
}
