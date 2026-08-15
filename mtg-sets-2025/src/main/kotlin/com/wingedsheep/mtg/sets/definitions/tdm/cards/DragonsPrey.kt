package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Dragon's Prey
 * {2}{B}
 * Instant
 *
 * This spell costs {2} more to cast if it targets a Dragon.
 * Destroy target creature.
 *
 * The cost clause is modeled with [CostModification.IncreaseGenericIfAnyTargetMatches] on a
 * [SpellCostTarget.SelfCast] static ability — the increase analogue of Dire Downdraft's
 * "{1} less if it targets ..." reduction. The increase is locked in from the chosen target at
 * cast time (CR 601.2f); the spell's only target is the destroyed creature, so it raises the
 * cost exactly when that creature is a Dragon.
 */
val DragonsPrey = card("Dragon's Prey") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "This spell costs {2} more to cast if it targets a Dragon.\n" +
        "Destroy target creature."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Destroy(creature)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.IncreaseGenericIfAnyTargetMatches(
                amount = 2,
                filter = GameObjectFilter.Creature.withSubtype(Subtype.DRAGON),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a6004ff-4180-4332-8b51-960f8c7521d9.jpg?1743204277"
    }
}
