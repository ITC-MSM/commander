package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bloodflow Connoisseur
 * {2}{B}
 * Creature — Vampire
 * 1 / 1
 *
 * Sacrifice a creature: Put a +1/+1 counter on this creature.
 */
val BloodflowConnoisseur = card("Bloodflow Connoisseur") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 1
    toughness = 1
    oracleText = "Sacrifice a creature: Put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Slawomir Maniak"
        flavorText = "\"Death not for survival but for vanity and pleasure? This is the decadence I sought to curb.\"\n—Sorin Markov"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97485dbf-2f31-4ed2-a6cd-529ca22c9ac5.jpg?1783940706"
    }
}
