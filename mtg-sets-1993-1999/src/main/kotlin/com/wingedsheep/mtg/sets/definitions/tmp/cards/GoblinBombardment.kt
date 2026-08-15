package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Goblin Bombardment
 * {1}{R}
 * Enchantment
 *
 * Sacrifice a creature: This enchantment deals 1 damage to any target.
 */
val GoblinBombardment = card("Goblin Bombardment") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Sacrifice a creature: This enchantment deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
        description = "Sacrifice a creature: This enchantment deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Brian Snõddy"
        flavorText = "One mogg to aim the catapult, one mogg to steer the rock."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/179e954f-1d90-4ef4-b800-25845cc338e2.jpg?1562052788"
    }
}
