package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Reckless Assault
 * {2}{B}{R}
 * Enchantment
 * {1}, Pay 2 life: This enchantment deals 1 damage to any target.
 */
val RecklessAssault = card("Reckless Assault") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Enchantment"
    oracleText = "{1}, Pay 2 life: This enchantment deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.PayLife(2))
        target = AnyTarget()
        effect = Effects.DealDamage(1, EffectTarget.ContextTarget(0))
        description = "{1}, Pay 2 life: This enchantment deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "263"
        artist = "Jeff Easley"
        flavorText = "\"How will you fight an enemy that cares nothing for itself?\"\n—The Blind Seer"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff0f568e-4d3a-40a5-b72a-63040ec5402d.jpg?1562946506"
    }
}
