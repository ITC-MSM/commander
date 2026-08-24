package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Kird Chieftain
 * {3}{R}
 * Creature — Ape
 * 3/3
 * This creature gets +1/+1 as long as you control a Forest.
 * {4}{G}: Target creature gets +2/+2 and gains trample until end of turn.
 */
val KirdChieftain = card("Kird Chieftain") {
    manaCost = "{3}{R}"
    colorIdentity = "GR"
    typeLine = "Creature — Ape"
    power = 3
    toughness = 3
    oracleText =
        "This creature gets +1/+1 as long as you control a Forest.\n" +
        "{4}{G}: Target creature gets +2/+2 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+1, +1, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, Filters.ForestCard)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{4}{G}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, t))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Lars Grant-West"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/4189a909-2e20-4dc7-894c-21446da5b0cf.jpg?1783939173"
    }
}
