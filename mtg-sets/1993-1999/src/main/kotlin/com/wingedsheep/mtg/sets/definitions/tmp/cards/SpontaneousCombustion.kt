package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spontaneous Combustion
 * {1}{B}{R}
 * Instant
 * As an additional cost to cast this spell, sacrifice a creature.
 * Spontaneous Combustion deals 3 damage to each creature.
 */
val SpontaneousCombustion = card("Spontaneous Combustion") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\n" +
        "Spontaneous Combustion deals 3 damage to each creature."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature),
            Effects.DealDamage(3, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "273"
        artist = "Doug Chaffee"
        flavorText = "\"Heat of battle\" is usually a metaphor."
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34e6c04f-9d1a-497b-bc96-a0e48a1c1904.jpg"
    }
}
