package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Subtype
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
 * Water Wurm
 * {U}
 * Creature — Wurm
 * 1/1
 * This creature gets +0/+1 as long as an opponent controls an Island.
 *
 * The Kird Ape shape with the opponent as the reference player: an `Exists` over
 * [Player.EachOpponent]'s battlefield, re-asked on every projection.
 */
val WaterWurm = card("Water Wurm") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wurm"
    power = 1
    toughness = 1
    oracleText = "This creature gets +0/+1 as long as an opponent controls an Island."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(0, 1, Filters.Self),
            condition = Exists(
                Player.EachOpponent,
                Zone.BATTLEFIELD,
                GameObjectFilter.Land.withSubtype(Subtype.ISLAND)
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3da4a88-5225-467f-9240-f30bc1eee520.jpg?1783947941"

        ruling("2004-10-04", "Only gets the bonus once even if more than one opponent has an Island on the battlefield.")
    }
}
