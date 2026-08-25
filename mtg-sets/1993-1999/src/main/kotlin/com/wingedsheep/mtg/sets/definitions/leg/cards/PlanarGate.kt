package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Planar Gate
 * {6}
 * Artifact
 *
 * Creature spells you cast cost {2} less to cast.
 */
val PlanarGate = card("Planar Gate") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Creature spells you cast cost {2} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Creature),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "290"
        artist = "Melissa A. Benson"
        flavorText = "Nireya reached through the Gate, sensing the energies trapped beyond."
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd27f0fe-c032-4f61-9f3d-98a6d2e2c426.jpg?1783948026"
    }
}
