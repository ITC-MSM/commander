package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Pearl of Wisdom
 * {2}{U}
 * Sorcery
 *
 * This spell costs {1} less to cast if you control an Otter.
 * Draw two cards.
 */
val PearlOfWisdom = card("Pearl of Wisdom") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "This spell costs {1} less to cast if you control an Otter.\nDraw two cards."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Any.withSubtype("Otter"),
                ),
            ),
        )
    }

    spell {
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Julie Dillon"
        flavorText = "Otterfolk divers compete to bring up the biggest, purest pearls they can find. Storing elemental magic in them is a bonus."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13cb9575-1138-4f99-8e90-0eaf00bdf4a1.jpg?1721426185"
    }
}
