package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * First-Time Flyer
 * {1}{U}
 * Creature — Human Pilot Ally
 * 1/2
 * Flying
 * This creature gets +1/+1 as long as there's a Lesson card in your graveyard.
 */
val FirstTimeFlyer = card("First-Time Flyer") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pilot Ally"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "This creature gets +1/+1 as long as there's a Lesson card in your graveyard."
    keywords(Keyword.FLYING)

    // +1/+1 while there's at least one Lesson card in your graveyard.
    staticAbility {
        ability = ModifyStats(1, 1, Filters.Self)
        condition = Conditions.CompareAmounts(
            DynamicAmount.Count(
                Player.You,
                Zone.GRAVEYARD,
                GameObjectFilter.Any.withSubtype(Subtype.LESSON),
            ),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Mizutametori"
        flavorText = "Aang offered ancient Air Nomad wisdom to the newest generation of " +
            "glider riders: Keep your mouth closed, so you don't swallow any bugs."
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea22d29d-3a62-4ae2-91d5-21a678be9b48.jpg?1764120222"
    }
}
