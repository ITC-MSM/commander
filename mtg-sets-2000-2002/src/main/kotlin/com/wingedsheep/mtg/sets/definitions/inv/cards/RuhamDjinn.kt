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
 * Ruham Djinn
 * {5}{W}
 * Creature — Djinn
 * 5/5
 * First strike
 * This creature gets -2/-2 as long as white is the most common color among all permanents
 * or is tied for most common.
 */
val RuhamDjinn = card("Ruham Djinn") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Djinn"
    power = 5
    toughness = 5
    oracleText = "First strike\nThis creature gets -2/-2 as long as white is the most common color among all permanents or is tied for most common."

    keywords(Keyword.FIRST_STRIKE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = -2, toughnessBonus = -2, filter = GroupFilter.source()),
            condition = Conditions.ColorIsMostCommon(Color.WHITE)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Jeff Easley"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a46c7718-1ecc-418c-b213-13be9de5cb7f.jpg?1562928232"
    }
}
