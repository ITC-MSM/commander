package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Foundry Inspector
 * {3}
 * Artifact Creature — Construct
 * 3 / 2
 *
 * Artifact spells you cast cost {1} less to cast.
 *
 * A generic-only reduction on the controller's own artifact spells
 * ([SpellCostTarget.YouCast] over [GameObjectFilter.Artifact] with
 * [CostModification.ReduceGeneric]), so it never shaves a coloured pip. Like every cost static it
 * functions only from the battlefield, which is why it never discounts itself.
 */
val FoundryInspector = card("Foundry Inspector") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Construct"
    oracleText = "Artifact spells you cast cost {1} less to cast."
    power = 3
    toughness = 2

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Artifact),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Jason A. Engle"
        flavorText = "Automaton inspectors ensure that the Consulate's stringent standards for mass production are upheld."
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93f827e8-1cc4-4a15-a4be-2e74323963b9.jpg?1783937155"
    }
}
