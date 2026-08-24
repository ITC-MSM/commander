package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Souldrinker
 * {3}{B}
 * Creature — Spirit
 * 2/2
 * Pay 3 life: Put a +1/+1 counter on this creature.
 */
val Souldrinker = card("Souldrinker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 2
    oracleText = "Pay 3 life: Put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.PayLife(3)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "158"
        artist = "Dermot Power"
        flavorText = "Don't drink and thrive."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/07d2d0ff-e44e-427a-9d68-3ed2d51b1b86.jpg"
    }
}
