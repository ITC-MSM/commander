package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Goham Djinn
 * {5}{B}
 * Creature — Djinn
 * 5/5
 * {1}{B}: Regenerate this creature.
 * This creature gets -2/-2 as long as black is the most common color among all permanents
 * or is tied for most common.
 */
val GohamDjinn = card("Goham Djinn") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Djinn"
    power = 5
    toughness = 5
    oracleText = "{1}{B}: Regenerate this creature.\nThis creature gets -2/-2 as long as black is the most common color among all permanents or is tied for most common."

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = -2, toughnessBonus = -2, filter = GroupFilter.source()),
            condition = Conditions.ColorIsMostCommon(Color.BLACK)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d67796c7-4d93-4c50-8839-bb69e075bc42.jpg?1562938029"
    }
}
