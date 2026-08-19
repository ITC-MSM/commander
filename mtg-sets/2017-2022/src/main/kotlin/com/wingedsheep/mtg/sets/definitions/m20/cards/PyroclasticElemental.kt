package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pyroclastic Elemental
 * {3}{R}{R}
 * Creature — Elemental
 * 5/4
 *
 * {1}{R}{R}: This creature deals 1 damage to target player.
 */
val PyroclasticElemental = card("Pyroclastic Elemental") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "{1}{R}{R}: This creature deals 1 damage to target player."
    power = 5
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{1}{R}{R}")
        target = Targets.Player
        effect = Effects.DealDamage(1, EffectTarget.ContextTarget(0))
        description = "{1}{R}{R}: This creature deals 1 damage to target player."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "296"
        artist = "Svetlin Velinov"
        flavorText = "\"Whoever thought of making mobile volcanoes was a genius.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d57f8b5-fde9-498d-82bf-34bfb2370703.jpg?1783932918"
    }
}
