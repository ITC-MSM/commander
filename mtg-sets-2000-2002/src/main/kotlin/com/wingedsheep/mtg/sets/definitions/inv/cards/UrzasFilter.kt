package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Urza's Filter
 * {4}
 * Artifact
 * Multicolored spells cost {2} less to cast.
 *
 * Affects spells cast by any player (no controller restriction in the oracle text).
 */
val UrzasFilter = card("Urza's Filter") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Multicolored spells cost {2} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Multicolored),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "318"
        artist = "Dave Dorman"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/680c75b1-e766-40be-84d7-2332047bb3de.jpg?1562915969"
    }
}
