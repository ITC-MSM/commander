package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Falkenrath Torturer
 * {2}{B}
 * Creature — Vampire
 * 2/1
 * Sacrifice a creature: This creature gains flying until end of turn. If the sacrificed
 * creature was a Human, put a +1/+1 counter on this creature.
 *
 * The Human rider reads the cost-sacrificed permanent's snapshot via
 * [Conditions.SacrificedHadSubtype] (which inspects `EffectContext.sacrificedPermanents`,
 * captured at cost-payment time). "Sacrifice a creature" accepts any creature you control
 * including this one, so the cost is [Costs.Sacrifice] (not `SacrificeAnother`).
 */
val FalkenrathTorturer = card("Falkenrath Torturer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 1
    oracleText = "Sacrifice a creature: This creature gains flying until end of turn. " +
        "If the sacrificed creature was a Human, put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
            .then(
                ConditionalEffect(
                    condition = Conditions.SacrificedHadSubtype("Human"),
                    effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Steve Argyle"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e81d6ed-2141-4177-9ded-680fff65b39e.jpg?1782714615"
    }
}
