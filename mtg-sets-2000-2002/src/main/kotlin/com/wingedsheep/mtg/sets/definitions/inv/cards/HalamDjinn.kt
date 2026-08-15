package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Halam Djinn
 * {5}{R}
 * Creature — Djinn
 * 6/5
 * Haste
 * This creature gets -2/-2 as long as red is the most common color among all permanents
 * or is tied for most common.
 */
val HalamDjinn = card("Halam Djinn") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Djinn"
    power = 6
    toughness = 5
    oracleText = "Haste\nThis creature gets -2/-2 as long as red is the most common color among all permanents or is tied for most common."

    keywords(Keyword.HASTE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = -2, toughnessBonus = -2, filter = GroupFilter.source()),
            condition = Conditions.ColorIsMostCommon(Color.RED)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/369ade1f-e909-47ae-bb01-19588269ad8f.jpg?1562906010"
    }
}
