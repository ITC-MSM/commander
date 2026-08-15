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
 * Zanam Djinn
 * {5}{U}
 * Creature — Djinn
 * 5/6
 * Flying
 * This creature gets -2/-2 as long as blue is the most common color among all permanents
 * or is tied for most common.
 */
val ZanamDjinn = card("Zanam Djinn") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn"
    power = 5
    toughness = 6
    oracleText = "Flying\nThis creature gets -2/-2 as long as blue is the most common color among all permanents or is tied for most common."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = -2, toughnessBonus = -2, filter = GroupFilter.source()),
            condition = Conditions.ColorIsMostCommon(Color.BLUE)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Eric Peterson"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57a3c1d5-0ca8-443b-ae7a-66e0363e377b.jpg?1562912806"
    }
}
