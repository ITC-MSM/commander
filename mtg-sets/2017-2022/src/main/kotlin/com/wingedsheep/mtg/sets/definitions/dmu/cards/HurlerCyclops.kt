package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Hurler Cyclops
 * {3}{R}{R}
 * Creature — Cyclops
 * 5/4
 * {1}, Sacrifice another creature: This creature deals 1 damage to any target.
 */
val HurlerCyclops = card("Hurler Cyclops") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cyclops"
    oracleText = "{1}, Sacrifice another creature: This creature deals 1 damage to any target."
    power = 5
    toughness = 4

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        val t = target("any target", AnyTarget())
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Xavier Ribeiro"
        flavorText = "\"It's my fault, really. I told him to throw anything he could find at the enemy.\"\n—Throppel, auxiliary commander"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b16f2cf2-5908-4f49-9c5c-6c9e22a65a4d.jpg?1783921316"
    }
}
