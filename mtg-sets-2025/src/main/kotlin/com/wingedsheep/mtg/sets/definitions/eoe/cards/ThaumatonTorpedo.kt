package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Thaumaton Torpedo
 * {1}
 * Artifact
 *
 * {6}, {T}, Sacrifice this artifact: Destroy target nonland permanent.
 * This ability costs {3} less to activate if you attacked with a Spacecraft this turn.
 */
val ThaumatonTorpedo = card("Thaumaton Torpedo") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "{6}, {T}, Sacrifice this artifact: Destroy target nonland permanent. " +
        "This ability costs {3} less to activate if you attacked with a Spacecraft this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap, Costs.SacrificeSelf)
        val permanent = target(
            "target nonland permanent",
            TargetPermanent(filter = TargetFilter.NonlandPermanent)
        )
        effect = Effects.Destroy(permanent)
        genericCostReduction = DynamicAmount.Conditional(
            condition = Conditions.YouAttackedWithCreaturesThisTurn(
                filter = GameObjectFilter.Permanent.withSubtype("Spacecraft"),
                atLeast = 1
            ),
            ifTrue = DynamicAmount.Fixed(3),
            ifFalse = DynamicAmount.Fixed(0)
        )
        description = "{6}, {T}, Sacrifice this artifact: Destroy target nonland permanent. " +
            "This ability costs {3} less to activate if you attacked with a Spacecraft this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Madeline Boni"
        flavorText = "A miniature star, replicating the mana-aether reactions in a sun's core."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/1817f1b5-960a-435c-bdec-8cc8cbcb3358.jpg?1752947563"
    }
}
