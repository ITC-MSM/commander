package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Crackling Triton
 * {2}{U}
 * Creature — Merfolk Wizard
 * 2 / 3
 *
 * {2}{R}, Sacrifice this creature: It deals 2 damage to any target.
 *
 * "It" is the sacrificed creature itself, which is the effect's default damage source — no explicit
 * `damageSource` belongs here.
 */
val CracklingTriton = card("Crackling Triton") {
    manaCost = "{2}{U}"
    colorIdentity = "UR"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 3
    oracleText = "{2}{R}, Sacrifice this creature: It deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{R}"),
            Costs.SacrificeSelf
        )
        val t = target("any target", AnyTarget())
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Greg Staples"
        flavorText = "He calls upon both the currents in the sea and the current in the clouds."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d6bb19c-3610-4609-b23c-21d4d80e4582.jpg"
    }
}
