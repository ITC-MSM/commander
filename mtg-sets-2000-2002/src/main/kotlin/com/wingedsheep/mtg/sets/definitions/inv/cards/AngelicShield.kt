package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Angelic Shield
 * {W}{U}
 * Enchantment
 *
 * Creatures you control get +0/+1.
 * Sacrifice this enchantment: Return target creature to its owner's hand.
 */
val AngelicShield = card("Angelic Shield") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Enchantment"
    oracleText = "Creatures you control get +0/+1.\n" +
        "Sacrifice this enchantment: Return target creature to its owner's hand."

    // Creatures you control get +0/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    // Sacrifice this enchantment: Return target creature to its owner's hand.
    activatedAbility {
        cost = Costs.SacrificeSelf
        target = Targets.Creature
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "228"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5aaa3e4e-4e08-4df2-9e0c-66e15a10fec4.jpg?1562913430"
    }
}
