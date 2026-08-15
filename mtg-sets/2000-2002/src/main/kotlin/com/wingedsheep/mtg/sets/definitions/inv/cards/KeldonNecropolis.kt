package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Keldon Necropolis
 * Legendary Land
 * {T}: Add {C}.
 * {4}{R}, {T}, Sacrifice a creature: Keldon Necropolis deals 2 damage to any target.
 */
val KeldonNecropolis = card("Keldon Necropolis") {
    typeLine = "Legendary Land"
    colorIdentity = "R"
    oracleText = "{T}: Add {C}.\n" +
        "{4}{R}, {T}, Sacrifice a creature: Keldon Necropolis deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}{R}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Creature),
        )
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, anyTarget, damageSource = EffectTarget.Self)
        description = "{4}{R}, {T}, Sacrifice a creature: Keldon Necropolis deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "325"
        artist = "Franz Vohwinkel"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f0cccf6-b79b-4fff-89aa-801341598532.jpg?1562911005"
    }
}
