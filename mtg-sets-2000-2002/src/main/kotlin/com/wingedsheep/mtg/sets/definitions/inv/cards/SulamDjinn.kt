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
 * Sulam Djinn
 * {5}{G}
 * Creature — Djinn
 * 6/6
 * Trample
 * This creature gets -2/-2 as long as green is the most common color among all permanents
 * or is tied for most common.
 */
val SulamDjinn = card("Sulam Djinn") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Djinn"
    power = 6
    toughness = 6
    oracleText = "Trample\nThis creature gets -2/-2 as long as green is the most common color among all permanents or is tied for most common."

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = -2, toughnessBonus = -2, filter = GroupFilter.source()),
            condition = Conditions.ColorIsMostCommon(Color.GREEN)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "212"
        artist = "Greg Hildebrandt & Tim Hildebrandt"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7aeab16f-e104-47e7-81c7-b6e0123120d7.jpg?1562919728"
    }
}
