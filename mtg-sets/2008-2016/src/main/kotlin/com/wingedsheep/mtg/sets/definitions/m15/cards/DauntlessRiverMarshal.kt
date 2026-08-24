package com.wingedsheep.mtg.sets.definitions.m15.cards

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
 * Dauntless River Marshal
 * {1}{W}
 * Creature — Human Soldier
 * 2/1
 * This creature gets +1/+1 as long as you control an Island.
 * {3}{U}: Tap target creature.
 *
 * The pump is a [ConditionalStaticAbility] over [GroupFilter.source] — re-evaluated in Layer 7c, so
 * it comes and goes with the Island. The activated ability's blue cost is why the card's colour
 * identity is `UW` while its mana cost is mono-white.
 */
val DauntlessRiverMarshal = card("Dauntless River Marshal") {
    manaCost = "{1}{W}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1
    oracleText =
        "This creature gets +1/+1 as long as you control an Island.\n" +
        "{3}{U}: Tap target creature."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+1, +1, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, Filters.IslandCard)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Mark Winters"
        flavorText = "\"Thieves and squid squirm the same way when you capture them.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efedfd1e-5073-47e2-9137-5f2bf4e436e3.jpg?1783939203"
    }
}
