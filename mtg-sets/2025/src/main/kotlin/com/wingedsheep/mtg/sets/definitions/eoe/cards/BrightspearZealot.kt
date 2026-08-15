package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Brightspear Zealot
 * {2}{W}
 * Creature — Human Soldier
 * Vigilance
 * This creature gets +2/+0 as long as you've cast two or more spells this turn.
 * 2/4
 */
val BrightspearZealot = card("Brightspear Zealot") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 4
    oracleText = "Vigilance\nThis creature gets +2/+0 as long as you've cast two or more spells this turn."

    keywords(Keyword.VIGILANCE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantDynamicStatsEffect(
                filter = GroupFilter.source(),
                powerBonus = DynamicAmount.Fixed(2),
                toughnessBonus = DynamicAmount.Fixed(0)
            ),
            condition = Conditions.YouCastSpellsThisTurn(atLeast = 2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Bryan Sola"
        flavorText = "\"My purpose is my spear. My spear is my life. My life is for the Regent Maximum.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7f7541a-5910-4f33-8c1d-3507ce3a426e.jpg?1752946584"
    }
}
