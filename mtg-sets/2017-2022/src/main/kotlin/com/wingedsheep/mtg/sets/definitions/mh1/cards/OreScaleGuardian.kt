package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Ore-Scale Guardian
 * {5}{R}{R}
 * Creature — Dragon
 * 4/4
 * This spell costs {1} less to cast for each land card in your graveyard.
 * Flying, haste
 *
 * A self-cast [ModifySpellCost] / [CostModification.ReduceGenericBy] over
 * [CostReductionSource.CardsInGraveyardMatchingFilter] — the graveyard is base state, so the
 * filter is the plain [GameObjectFilter.Land] with no controller predicate ("your graveyard" is
 * the source's own, not a filter clause). Like affinity this only shaves generic mana, so the
 * {R}{R} always has to be paid and the mana value stays 7 in every zone.
 */
val OreScaleGuardian = card("Ore-Scale Guardian") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "This spell costs {1} less to cast for each land card in your graveyard.\n" +
        "Flying, haste"

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(
                    filter = GameObjectFilter.Land
                )
            )
        )
    }

    keywords(Keyword.FLYING, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Aaron Miller"
        flavorText = "A dragon's loyalty cannot be earned, but it can be bought."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e789333b-21ee-4613-8eba-52a719b0f1e5.jpg?1783933108"
    }
}
