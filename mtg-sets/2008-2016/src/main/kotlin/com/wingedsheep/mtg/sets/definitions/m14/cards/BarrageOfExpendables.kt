package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Barrage of Expendables
 * {R}
 * Enchantment
 *
 * {R}, Sacrifice a creature: This enchantment deals 1 damage to any target.
 */
val BarrageOfExpendables = card("Barrage of Expendables") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "{R}, Sacrifice a creature: This enchantment deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Sacrifice(GameObjectFilter.Creature))
        target = Targets.Any
        effect = Effects.DealDamage(1, EffectTarget.ContextTarget(0))
        description = "{R}, Sacrifice a creature: This enchantment deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Trevor Claxton"
        flavorText = "Goblin generals don't distinguish between troops and ammunition."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9e0912d-b4b9-497c-bce7-ed80b79bab32.jpg?1783939918"
    }
}
