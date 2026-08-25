package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Weaponize the Monsters
 * {R}
 * Enchantment
 *
 * {2}, Sacrifice a creature: This enchantment deals 2 damage to any target.
 *
 * The Stormbind shape: a [Costs.Composite] of the mana atom and a sacrifice atom over
 * [GameObjectFilter.Creature]. The sacrifice is a *cost*, so it happens on activation and can't be
 * responded to — the creature is already gone by the time the ability resolves, and the ability
 * still deals its damage if the creature is somehow saved after.
 */
val WeaponizeTheMonsters = card("Weaponize the Monsters") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "{2}, Sacrifice a creature: This enchantment deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Sacrifice(GameObjectFilter.Creature))
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "140"
        artist = "Magali Villeneuve"
        flavorText = "Revenge is a path inevitably walked alone."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68bba622-a0ab-4c0e-88b1-9120690ea5a0.jpg"
    }
}
