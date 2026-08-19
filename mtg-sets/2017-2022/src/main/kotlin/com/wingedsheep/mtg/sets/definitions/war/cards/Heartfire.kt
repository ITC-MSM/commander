package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Heartfire
 * {1}{R}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature or planeswalker.
 * Heartfire deals 4 damage to any target.
 */
val Heartfire = card("Heartfire") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or planeswalker.\nHeartfire deals 4 damage to any target."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.CreatureOrPlaneswalker))

    spell {
        target = Targets.Any
        effect = Effects.DealDamage(4, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Craig J Spearing"
        flavorText = "The mage looked within and realized there was still one piece of fuel to burn."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7db219ea-2ed1-4a86-955c-d61ecedbc019.jpg?1783933427"
    }
}
