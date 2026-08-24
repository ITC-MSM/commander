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
 * Jorubai Murk Lurker
 * {2}{U}
 * Creature — Leech
 * 1/3
 * This creature gets +1/+1 as long as you control a Swamp.
 * {1}{B}: Target creature gains lifelink until end of turn.
 */
val JorubaiMurkLurker = card("Jorubai Murk Lurker") {
    manaCost = "{2}{U}"
    colorIdentity = "BU"
    typeLine = "Creature — Leech"
    power = 1
    toughness = 3
    oracleText =
        "This creature gets +1/+1 as long as you control a Swamp.\n" +
        "{1}{B}: Target creature gains lifelink until end of turn. (Damage dealt by the creature also causes its controller to gain that much life.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+1, +1, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, Filters.SwampCard)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.LIFELINK, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Clint Cearley"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7af346ac-32a6-49a8-986d-834b2c8c0478.jpg?1783939190"
    }
}
