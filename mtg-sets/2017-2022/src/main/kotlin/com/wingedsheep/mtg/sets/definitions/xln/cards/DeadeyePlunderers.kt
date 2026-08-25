package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Deadeye Plunderers
 * {3}{U}{B}
 * Creature — Human Pirate
 * 3/3
 *
 * This creature gets +1/+1 for each artifact you control.
 * {2}{U}{B}: Create a Treasure token.
 */
val DeadeyePlunderers = card("Deadeye Plunderers") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Human Pirate"
    oracleText = "This creature gets +1/+1 for each artifact you control.\n" +
        "{2}{U}{B}: Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: " +
        "Add one mana of any color.\")"
    power = 3
    toughness = 3

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count(),
            toughnessBonus = DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count(),
        )
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}{B}")
        effect = Effects.CreateTreasure()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Greg Opalinski"
        flavorText = "\"Keep your friends close and your enemies within range.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63a7a1a4-aec2-467d-91a1-1a2605718c7c.jpg"
    }
}
