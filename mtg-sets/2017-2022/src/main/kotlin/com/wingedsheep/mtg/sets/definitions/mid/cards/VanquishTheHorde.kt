package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Vanquish the Horde
 * {6}{W}{W}
 * Sorcery
 * This spell costs {1} less to cast for each creature on the battlefield.
 * Destroy all creatures.
 */
val VanquishTheHorde = card("Vanquish the Horde") {
    manaCost = "{6}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "This spell costs {1} less to cast for each creature on the battlefield.\n" +
        "Destroy all creatures."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsOnBattlefieldMatching(Filters.Creature)
            ),
        )
    }

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Grzegorz Rutkowski"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e264615c-eb99-4cb3-844a-2b4a94ba5203.jpg?1782703708"
    }
}
