package com.wingedsheep.mtg.sets.definitions.lci.cards

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
 * Out of Air
 * {2}{U}{U}
 * Instant
 * This spell costs {2} less to cast if it targets a creature spell.
 * Counter target spell.
 *
 * The spell counters any target spell (`Targets.Spell`); the conditional discount is a
 * self-cost reduction gated on the chosen target being a creature spell. Modeled exactly like
 * Brush Off's generic half — [ModifySpellCost] on [SpellCostTarget.SelfCast] with
 * [CostModification.ReduceGenericBy] over [CostReductionSource.FixedIfAnyTargetMatches], filtered
 * to [GameObjectFilter.Creature] (a creature *spell on the stack*, the same way Brush Off's
 * `InstantOrSorcery` filter matches an instant/sorcery spell). The reduction is generic-only, so
 * no colored-pip reduction is needed. It resolves at cast time once the target is announced
 * (CR 601.2f): if the target is a creature spell the generic cost drops by {2} to {U}{U}.
 */
val OutOfAir = card("Out of Air") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if it targets a creature spell.\n" +
        "Counter target spell."

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell()
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 2,
                    filter = GameObjectFilter.Creature,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Francisco Miyara"
        flavorText = "\"The Core is not a simple trove to be plundered by intruders from the surface. " +
            "It will reject those who do not accord it respect.\"\n—Akal Pakal, First Steward of Oteclan"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c263db55-fcac-4b49-b626-7c8092accfcd.jpg?1782694555"
    }
}
