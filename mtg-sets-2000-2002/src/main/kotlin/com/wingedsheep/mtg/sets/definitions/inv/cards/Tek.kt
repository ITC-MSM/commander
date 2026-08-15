package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Tek
 * {5}
 * Artifact Creature — Dragon
 * 2/2
 * This creature gets +0/+2 as long as you control a Plains, has flying as long as you
 * control an Island, gets +2/+0 as long as you control a Swamp, has first strike as long
 * as you control a Mountain, and has trample as long as you control a Forest.
 */
val Tek = card("Tek") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 2
    toughness = 2
    oracleText = "This creature gets +0/+2 as long as you control a Plains, has flying as long " +
        "as you control an Island, gets +2/+0 as long as you control a Swamp, has first strike " +
        "as long as you control a Mountain, and has trample as long as you control a Forest."

    // +0/+2 as long as you control a Plains
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 0, toughnessBonus = 2, filter = Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
        )
    }

    // Flying as long as you control an Island
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Island"))
        )
    }

    // +2/+0 as long as you control a Swamp
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp"))
        )
    }

    // First strike as long as you control a Mountain
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain"))
        )
    }

    // Trample as long as you control a Forest
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest"))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "313"
        artist = "Chippy"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1f38104-a699-4bb9-930a-699f7bbc338a.jpg?1562933962"
    }
}
