package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CompositeStaticAbility
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Kithkin Greatheart
 * {1}{W}
 * Creature — Kithkin Soldier
 * 2/1
 * As long as you control a Giant, this creature gets +1/+1 and has first strike.
 */
val KithkinGreatheart = card("Kithkin Greatheart") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 2
    toughness = 1
    oracleText = "As long as you control a Giant, this creature gets +1/+1 and has first strike."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CompositeStaticAbility(
                listOf(
                    ModifyStats(powerBonus = 1, toughnessBonus = 1, filter = GroupFilter.source()),
                    GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.source())
                )
            ),
            condition = Conditions.ControlPermanentOfType(Subtype.GIANT)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Greg Staples"
        flavorText = "Sometimes a curious giant singles out a \"little one\" to follow for a few days, never realizing the effect it will have on the little one's life."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e75283b6-1b70-4e89-bf21-f85c21454aae.jpg?1783942912"
    }
}
