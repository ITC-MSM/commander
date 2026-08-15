package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Kird Ape
 * {R}
 * Creature — Ape
 * 1/1
 * This creature gets +1/+2 as long as you control a Forest.
 */
val KirdApe = card("Kird Ape") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ape"
    power = 1
    toughness = 1
    oracleText = "This creature gets +1/+2 as long as you control a Forest."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 2, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Forest")),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Ken Meyer, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/ebe8845e-df1c-481c-949c-aab84af99a05.jpg?1562939239"
    }
}
