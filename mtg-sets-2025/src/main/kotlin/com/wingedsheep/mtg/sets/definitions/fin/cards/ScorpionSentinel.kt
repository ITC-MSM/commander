package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Scorpion Sentinel
 * {1}{U}
 * Artifact Creature — Robot Scorpion
 * 1/4
 * As long as you control seven or more lands, this creature gets +3/+0.
 */
val ScorpionSentinel = card("Scorpion Sentinel") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Robot Scorpion"
    power = 1
    toughness = 4
    oracleText = "As long as you control seven or more lands, this creature gets +3/+0."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 3, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.ControlLandsAtLeast(7)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "HAISIRO"
        flavorText = "\"Barret, be careful! Attack while its tail's up, it's gonna counterattack with its laser.\"\n—Cloud Strife"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08ab5220-e5c1-472e-8217-97fd60e1773c.jpg?1748706024"
    }
}
