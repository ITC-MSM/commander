package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Coastal Bulwark
 * {2}
 * Artifact Creature — Wall
 * 1/3
 * Defender
 * This creature gets +2/+0 as long as you control an Island.
 * {2}, {T}: Surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)
 */
val CoastalBulwark = card("Coastal Bulwark") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Wall"
    power = 1
    toughness = 3
    oracleText = "Defender\nThis creature gets +2/+0 as long as you control an Island.\n" +
        "{2}, {T}: Surveil 1. (Look at the top card of your library. You may put that card into your graveyard.)"

    keywords(Keyword.DEFENDER)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(2, 0, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Island")),
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Artur Nakhodkin"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e24567f8-d195-4547-bba4-7a8131dc7889.jpg?1783920100"
    }
}
