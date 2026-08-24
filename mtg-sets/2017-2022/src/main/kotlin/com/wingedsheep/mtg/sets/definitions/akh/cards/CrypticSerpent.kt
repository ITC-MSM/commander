package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Cryptic Serpent — {5}{U}{U}
 * Creature — Serpent
 * 6/5
 *
 * This spell costs {1} less to cast for each instant and sorcery card in your graveyard.
 *
 * [CostReductionSource.CardsInGraveyardMatchingFilter] totals *cards*, which is what the printed
 * line asks for — five instants give {5} off. (Its distinct-types sibling `CardTypesInYourGraveyard`
 * would give {2} for any mix of instants and sorceries, which is a different card.) Only the generic
 * portion shrinks: the {U}{U} pips are never reduced, and the generic component floors at 0.
 *
 * AKH #48, Lius Lasahido.
 */
val CrypticSerpent = card("Cryptic Serpent") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 6
    toughness = 5
    oracleText = "This spell costs {1} less to cast for each instant and sorcery card in your graveyard."

    // {1} less for each instant and sorcery card in your graveyard
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(
                    filter = GameObjectFilter.InstantOrSorcery
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "48"
        artist = "Lius Lasahido"
        flavorText = "It slithers through the senses, constricting consciousness and poisoning perceptions."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43d11a24-8abf-46ff-8cc6-57b8ac3013f6.jpg?1783936524"
    }
}
