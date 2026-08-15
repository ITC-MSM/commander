package com.wingedsheep.mtg.sets.definitions.blb.cards

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
 * Dire Downdraft
 * {3}{U}
 * Instant
 *
 * This spell costs {1} less to cast if it targets an attacking or tapped creature.
 * Target creature's owner puts it on their choice of the top or bottom of their library.
 */
val DireDowndraft = card("Dire Downdraft") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if it targets an attacking or tapped creature.\nTarget creature's owner puts it on their choice of the top or bottom of their library."

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
                        statePredicates = listOf(
                            StatePredicate.Or(listOf(StatePredicate.IsAttacking, StatePredicate.IsTapped)),
                        ),
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Martin Wittfooth"
        flavorText = "Quickwing's elite flyers were more than capable of navigating ordinary storms, but Dragonhawk's storms were anything but ordinary."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1931f22-974c-43ad-911e-684bf3f9995d.jpg?1721426060"
    }
}
