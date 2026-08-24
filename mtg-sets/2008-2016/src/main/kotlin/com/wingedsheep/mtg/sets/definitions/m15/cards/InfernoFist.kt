package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Inferno Fist
 * {1}{R}
 * Enchantment — Aura
 * Enchant creature you control
 * Enchanted creature gets +2/+0.
 * {R}, Sacrifice this Aura: This Aura deals 2 damage to any target.
 */
val InfernoFist = card("Inferno Fist") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature you control\n" +
        "Enchanted creature gets +2/+0.\n" +
        "{R}, Sacrifice this Aura: This Aura deals 2 damage to any target."

    auraTarget = Targets.CreatureYouControl

    staticAbility {
        ability = ModifyStats(+2, 0)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.SacrificeSelf)
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "James Ryman"
        flavorText = "\"I've never been above throwing the first punch.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/721a42f4-5884-418f-b1f9-7cb651559ad0.jpg?1783939172"
    }
}
