package com.wingedsheep.mtg.sets.definitions.tmt.cards

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
 * Grounded for Life
 * {4}{W}
 * Instant
 *
 * This spell costs {3} less to cast if it targets a tapped creature.
 * Destroy target creature.
 */
val GroundedForLife = card("Grounded for Life") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "This spell costs {3} less to cast if it targets a tapped creature.\nDestroy target creature."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(amount = 3, filter = GameObjectFilter.Creature.tapped())
            )
        )
    }

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Andrea Tentori Montalto"
        flavorText = "\"Of course I know what you did! You always do precisely what I told you not to do!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72388199-85fa-4eba-9a9c-c2904e6da9ed.jpg?1771502482"
    }
}
