package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.predicates.StatePredicate

/**
 * Run Behind — Secrets of Strixhaven #66
 * {3}{U} · Instant
 *
 * This spell costs {1} less to cast if it targets an attacking creature.
 * Target creature's owner puts it on their choice of the top or bottom of their library.
 *
 * The cost-reduction-when-targeting sibling of Dire Downdraft: a `ModifySpellCost` static
 * (`CostReductionSource.FixedIfAnyTargetMatches`, generic-only) gated on the chosen target being
 * an attacking creature (`StatePredicate.IsAttacking`), plus the existing
 * `Effects.PutOnTopOrBottomOfLibrary` bounce where the *owner* chooses top or bottom.
 */
val RunBehind = card("Run Behind") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if it targets an attacking creature.\n" +
        "Target creature's owner puts it on their choice of the top or bottom of their library."

    spell {
        val creature = target("target creature to put on top or bottom of library", Targets.Creature)
        effect = Effects.PutOnTopOrBottomOfLibrary(creature)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 1,
                    filter = GameObjectFilter.Creature.copy(
                        statePredicates = listOf(StatePredicate.IsAttacking),
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Nereida"
        flavorText = "The skycoach waits for no one."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40ecc34b-4cd0-4998-bbf4-7faa6fd3d7e0.jpg?1775937369"
    }
}
