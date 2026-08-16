package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Orthodoxy Enforcer
 * {3}{W}
 * Creature — Phyrexian Cleric
 * 2/4
 *
 * Vigilance
 * This creature gets +2/+0 as long as you control two or more artifacts.
 *
 * The conditional buff is a continuous static modification gated by
 * [Conditions.YouControlAtLeast] over the artifact filter, recomputed at projection.
 */
val OrthodoxyEnforcer = card("Orthodoxy Enforcer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Phyrexian Cleric"
    power = 2
    toughness = 4
    oracleText = "Vigilance\n" +
        "This creature gets +2/+0 as long as you control two or more artifacts."

    keywords(Keyword.VIGILANCE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(2, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Xavier Ribeiro"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c938a0f-531c-4293-a89c-c7440d5acc5d.jpg?1783918076"
    }
}
