package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
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
 * Whack
 * {3}{B}
 * Sorcery
 * This spell costs {3} less to cast if it targets a white creature.
 * Target creature gets -4/-4 until end of turn.
 *
 * The discount is the Ride's End shape — [SpellCostTarget.SelfCast] over
 * [CostReductionSource.FixedIfAnyTargetMatches], a *generic* reduction gated on the spell's own
 * chosen target matching a white-creature filter.
 */
val Whack = card("Whack") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "This spell costs {3} less to cast if it targets a white creature.\nTarget creature gets -4/-4 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-4, -4, t)
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 3,
                    filter = GameObjectFilter.Creature.withColor(Color.WHITE),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "John Thacker"
        flavorText = "\"It's just business. You understand.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c862769d-f8bd-4cb6-b2b2-816179795f8b.jpg?1783923123"
    }
}
