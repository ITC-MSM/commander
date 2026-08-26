package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ember Hauler
 * {R}{R}
 * Creature — Goblin
 * 2/2
 *
 * {1}, Sacrifice this creature: It deals 2 damage to any target.
 *
 * The sacrifice is part of the activation cost ([Costs.SacrificeSelf]), not an effect, so the
 * Hauler is already in the graveyard when the ability resolves — "it" is the source, which is what
 * [Effects.DealDamage] uses by default, so no explicit `damageSource` is written.
 */
val EmberHauler = card("Ember Hauler") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 2
    toughness = 2
    oracleText = "{1}, Sacrifice this creature: It deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeSelf,
        )
        target = Targets.Any
        effect = Effects.DealDamage(2, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Steve Prescott"
        flavorText = "Flurk's crude goblin language didn't differentiate between \"I *bring* the flame\" and \"I *am* the flame.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9df1b79-bf3a-4da3-8d98-bdf175445f10.jpg?1783941806"
    }
}
