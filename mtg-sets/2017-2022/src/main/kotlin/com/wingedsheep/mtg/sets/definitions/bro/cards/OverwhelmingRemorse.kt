package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Overwhelming Remorse
 * {4}{B}
 * Instant
 * This spell costs {1} less to cast for each creature card in your graveyard.
 * Exile target creature or planeswalker.
 *
 * The Ghoultree cost line on an instant: a self-cast [ModifySpellCost] reducing generic mana by
 * [CostReductionSource.CardsInGraveyardMatchingFilter] over creature cards. The reduction only ever
 * eats generic, so the coloured {B} floor the rulings call out falls out of the modification itself
 * and the printed mana value stays 5.
 */
val OverwhelmingRemorse = card("Overwhelming Remorse") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast for each creature card in your graveyard.\n" +
        "Exile target creature or planeswalker."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(GameObjectFilter.Creature)
            )
        )
    }

    spell {
        val victim = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.Exile(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Ryan Valle"
        flavorText = "Tocasia's face haunted his vision. The explosion still rung in his ears. Mishra kept running and never once looked back."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/202cbfa4-3b3d-47fd-84a6-892692c906d6.jpg?1783920082"
        ruling("2022-10-14", "Overwhelming Remorse's first ability doesn't change its mana cost or mana value. It just reduces the cost to cast the spell.")
        ruling("2022-10-14", "Overwhelming Remorse's first ability can't reduce its cost to less than {B}.")
    }
}
