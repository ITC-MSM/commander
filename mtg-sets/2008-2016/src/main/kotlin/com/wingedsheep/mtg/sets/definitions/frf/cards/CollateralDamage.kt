package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Collateral Damage
 * {R}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Collateral Damage deals 3 damage to any target.
 */
val CollateralDamage = card("Collateral Damage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nCollateral Damage deals 3 damage to any target."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        target = Targets.Any
        effect = Effects.DealDamage(3, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Ryan Barger"
        flavorText = "It is much easier to create fire than to contain it."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb738362-b0b4-4811-9fbf-5f45c852c822.jpg?1783938691"
    }
}
