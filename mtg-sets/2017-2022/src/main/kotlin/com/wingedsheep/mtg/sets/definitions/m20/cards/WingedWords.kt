package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Winged Words
 * {2}{U}
 * Sorcery
 *
 * This spell costs {1} less to cast if you control a creature with flying.
 * Draw two cards.
 */
val WingedWords = card("Winged Words") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "This spell costs {1} less to cast if you control a creature with flying.\nDraw two cards."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
                )
            ),
        )
    }

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Chris Seaman"
        flavorText = "Magic written across the sky falls like rain on thirsty ground, bringing forth wisdom in its season."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff765065-b160-49b6-99ac-cd695bd0d903.jpg?1783933001"
    }
}
