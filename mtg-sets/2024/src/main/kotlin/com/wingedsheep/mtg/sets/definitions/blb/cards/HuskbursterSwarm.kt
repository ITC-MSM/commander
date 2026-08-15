package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Huskburster Swarm {7}{B}
 * Creature — Elemental Insect
 * 6/6
 *
 * This spell costs {1} less to cast for each creature card you own in exile
 * and in your graveyard.
 * Menace, deathtouch
 */
val HuskbursterSwarm = card("Huskburster Swarm") {
    manaCost = "{7}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elemental Insect"
    power = 6
    toughness = 6
    oracleText = "This spell costs {1} less to cast for each creature card you own in exile and in your graveyard.\nMenace, deathtouch"

    keywords(Keyword.MENACE, Keyword.DEATHTOUCH)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardAndExileMatchingFilter(
                    filter = GameObjectFilter.Creature,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed2f61d7-4eb0-41c5-8a34-a0793c2abc51.jpg?1721426437"
    }
}
