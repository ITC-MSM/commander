package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Ghoultree
 * {7}{G}
 * Creature — Zombie Treefolk
 * 10/10
 *
 * This spell costs {1} less to cast for each creature card in your graveyard.
 *
 * Cost reduction is a self-cast [ModifySpellCost] / [CostReductionSource.CardsInGraveyardMatchingFilter]
 * over creature cards in the caster's graveyard (mana value is unaffected — always 8).
 */
val Ghoultree = card("Ghoultree") {
    manaCost = "{7}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Zombie Treefolk"
    power = 10
    toughness = 10
    oracleText = "This spell costs {1} less to cast for each creature card in your graveyard."

    // This spell costs {1} less to cast for each creature card in your graveyard.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(
                    filter = GameObjectFilter.Creature,
                    amountPerCard = 1
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Volkan Baǵa"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a413c65e-5965-429b-8c25-11f8b73cba03.jpg?1782714579"
    }
}
