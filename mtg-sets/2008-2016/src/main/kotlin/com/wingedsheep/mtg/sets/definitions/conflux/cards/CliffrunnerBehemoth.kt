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

/**
 * Cliffrunner Behemoth — Conflux #79
 * {3}{G} · Creature — Rhino Beast · 5 / 3
 *
 * This creature has haste as long as you control a red permanent.
 * This creature has lifelink as long as you control a white permanent.
 *
 * Two printed sentences, two independent [ConditionalStaticAbility] rows — each a [GrantKeyword]
 * over [Filters.Self] (the source permanent) gated on its own condition, so the haste and the
 * lifelink switch on and off separately as the board changes. Each condition is
 * `Conditions.YouControl(GameObjectFilter.Permanent.withColor(...))`, an `Exists` over your
 * battlefield: a bare *permanent* filter, so a red land or artifact counts just as much as a red
 * creature. Compare [ToxicIguanar], which uses the same shape for a single grant.
 */
val CliffrunnerBehemoth = card("Cliffrunner Behemoth") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino Beast"
    power = 5
    toughness = 3
    oracleText = "This creature has haste as long as you control a red permanent.\n" +
        "This creature has lifelink as long as you control a white permanent."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HASTE, Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.RED))
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.LIFELINK, Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.WHITE))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "79"
        artist = "Wayne Reynolds"
        flavorText = "It's revered for its power, celebrated for its grace, and feared for the avalanches triggered by its thunderous feet."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/764c1a14-143f-4601-92c5-ebeabf3e375d.jpg"
    }
}
