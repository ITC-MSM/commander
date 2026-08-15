package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Coalition Victory
 * {3}{W}{U}{B}{R}{G}
 * Sorcery
 * You win the game if you control a land of each basic land type and a creature of each color.
 *
 * Composed from existing primitives (Invasion engine gap analysis #2): the "of each color" /
 * "of each basic land type" checks are distinct-value aggregations capped at 5, so each side is a
 * [Compare] against `Fixed(5)`. Per the 2006-09-25 ruling, a single multi-type land and/or a single
 * multicolored creature counts toward every type/color it has — which `DISTINCT_COLORS` /
 * `DISTINCT_BASIC_LAND_SUBTYPES` already model.
 */
val CoalitionVictory = card("Coalition Victory") {
    manaCost = "{3}{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Sorcery"
    oracleText = "You win the game if you control a land of each basic land type and a creature of each color."

    spell {
        effect = ConditionalEffect(
            condition = Conditions.All(
                // A land of each basic land type (domain = 5).
                Conditions.BasicLandTypesAtLeast(5),
                // A creature of each color (5 distinct colors among creatures you control).
                Compare(
                    DynamicAmounts.colorsAmongPermanents(Player.You, GameObjectFilter.Creature),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(5)
                )
            ),
            effect = Effects.WinGame(
                message = "Coalition Victory: controlled a land of each basic land type and a creature of each color."
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Eric Peterson"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd8ad3aa-3225-45ae-8343-5991f5b52269.jpg?1745319962"
        ruling(
            "2006-09-25",
            "When Coalition Victory resolves, it checks for the five basic land types (Plains, " +
                "Island, Swamp, Mountain, Forest) and the five colors (white, blue, black, red, " +
                "green). If a single land has multiple types and/or a single creature is multiple " +
                "colors, it will count all those types and/or colors."
        )
    }
}
