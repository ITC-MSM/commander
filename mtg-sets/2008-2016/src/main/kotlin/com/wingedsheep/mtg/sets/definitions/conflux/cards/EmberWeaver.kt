package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Ember Weaver
 * {2}{G}
 * Creature — Spider
 * 2/3
 * Reach (This creature can block creatures with flying.)
 * As long as you control a red permanent, this creature gets +1/+0 and has first strike.
 *
 * Reach is a printed [Keyword]. The domain-lord line is two [ConditionalStaticAbility] statics
 * sharing one condition — [ModifyStats] for the +1/+0 and [GrantKeyword] for first strike — over
 * [Filters.Self], which is `GroupFilter.source()`: the static reads *projected* battlefield state,
 * never the card's own type line.
 *
 * "A red **permanent**", not a red creature: the condition is
 * [Conditions.YouControl] over `GameObjectFilter.Permanent.withColor(RED)`, the same shape
 * Shadowmoor's Horde of Boggarts counts with.
 */
val EmberWeaver = card("Ember Weaver") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 2
    toughness = 3
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "As long as you control a red permanent, this creature gets +1/+0 and has first strike."

    keywords(Keyword.REACH)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 1, toughnessBonus = 0, filter = Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.RED))
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.RED))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Steve Prescott"
        flavorText = "\"Each night, the sun unravels and blows away. Each day, the spiders set a new one in the sky.\" —Sunseeder myth"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/10effbe2-fd8e-44b6-a08c-3984a92254d9.jpg"
    }
}
