package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Spectral Denial
 * {X}{U}
 * Instant
 * This spell costs {1} less to cast for each creature you control with power 4 or greater.
 * Counter target spell unless its controller pays {X}.
 *
 * The cost reduction counts only creatures the caster controls with power 4 or greater
 * (PermanentsOnBattlefieldMatching honors the controller/power predicates in the filter),
 * and the counter resolves against the spell's chosen X via the same XValue pump used by
 * Mindswipe.
 */
val SpectralDenial = card("Spectral Denial") {
    manaCost = "{X}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast for each creature you control with power " +
        "4 or greater.\nCounter target spell unless its controller pays {X}."

    spell {
        target = Targets.Spell
        effect = Effects.CounterUnlessDynamicPays(DynamicAmount.XValue)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsOnBattlefieldMatching(
                    GameObjectFilter.Creature.youControl().powerAtLeast(4)
                )
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Xabi Gaztelua"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee4e732a-1ffd-463d-92c2-26187659cfc3.jpg?1743204193"
    }
}
