package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ingenious Leonin
 * {4}{W}
 * Creature — Cat Soldier
 * 4/4
 * {3}{W}: Put a +1/+1 counter on another target attacking creature you control. If that creature
 *   is a Cat, it gains first strike until end of turn.
 *
 * Canonical printing lives in Jumpstart 2022 (the earliest real printing); Foundations is a
 * [com.wingedsheep.sdk.model.Printing] row (see `.../definitions/fdn/cards/IngeniousLeoninReprint.kt`).
 *
 * The activated ability always adds the counter; the first-strike grant is a resolution-time state
 * test ([ConditionalEffect], lowering to `Gate.WhenCondition`) on the chosen target being a Cat —
 * [Conditions.TargetMatchesFilter] reads the same target index the counter landed on.
 */
val IngeniousLeonin = card("Ingenious Leonin") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Soldier"
    power = 4
    toughness = 4
    oracleText = "{3}{W}: Put a +1/+1 counter on another target attacking creature you control. " +
        "If that creature is a Cat, it gains first strike until end of turn. (It deals combat " +
        "damage before creatures without first strike.)"

    activatedAbility {
        cost = Costs.Mana("{3}{W}")
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter.Creature.attacking().youControl().other()),
        )
        effect = Effects.Composite(
            listOf(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t),
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(
                        GameObjectFilter.Creature.withSubtype("Cat"),
                    ),
                    effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Eric Deschamps"
        flavorText = "Leonin claws were already deadly weapons of natural evolution before he " +
            "improved upon them."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c6fd0bf-3f02-46f3-9c9a-931eab190584.jpg?1782699361"
    }
}
