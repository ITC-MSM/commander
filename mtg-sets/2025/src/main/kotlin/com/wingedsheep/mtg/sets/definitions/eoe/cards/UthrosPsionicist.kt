package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Uthros Psionicist
 * {2}{U}
 * Creature — Jellyfish Scientist
 * 2/4
 *
 * The second spell you cast each turn costs {2} less to cast.
 *
 * Rulings (Scryfall, 2025-07-25):
 * - Cost reduction is applied after cost increases (CR 601.2f).
 * - Uthros Psionicist itself counts toward the per-turn spell tally. If it was the first spell
 *   you cast this turn, the next spell you cast is your second.
 */
val UthrosPsionicist = card("Uthros Psionicist") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Jellyfish Scientist"
    oracleText = "The second spell you cast each turn costs {2} less to cast."
    power = 2
    toughness = 4

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.NthOfTypePerTurn(2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Inkognit"
        flavorText = "\"Log entry cycle 33,507: Tenuous perception of psionic activity emanating from Uthros's core. Nature: Nonlinguistic, fragmentary. Content: Isolation, apoplexy. It wants free.\"\n—Uthros Combine research notes"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e23cc5fd-afe4-480c-8858-ed80a082584e.jpg?1752946892"
    }
}
