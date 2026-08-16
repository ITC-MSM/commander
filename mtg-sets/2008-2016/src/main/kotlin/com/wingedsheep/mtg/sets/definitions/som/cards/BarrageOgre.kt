package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Barrage Ogre
 * {3}{R}{R}
 * Creature — Ogre Warrior
 * 3/3
 * {T}, Sacrifice an artifact: This creature deals 2 damage to any target.
 *
 * The same shape as Orcish Vandal: a [Costs.Composite] of [Costs.Tap] and
 * [Costs.Sacrifice](Artifact), dealing 2 damage to [Targets.Any].
 */
val BarrageOgre = card("Barrage Ogre") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 3
    toughness = 3
    oracleText = "{T}, Sacrifice an artifact: This creature deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "David Rapoza"
        flavorText = "The elves had devised countless strategies to combat Memnarch's war machines, but they had no idea what to do when one was *thrown* at them."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e02c6f71-2448-47e1-9133-7af6a4d4577a.jpg?1783941726"
    }
}
