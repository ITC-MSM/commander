package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Veteran of the Depths
 * {3}{W}
 * Creature — Merfolk Soldier
 * 2/2
 * Whenever this creature becomes tapped, you may put a +1/+1 counter on it.
 *
 * "on it" is the source itself, so the counter goes on [EffectTarget.Self] — nothing is targeted.
 */
val VeteranOfTheDepths = card("Veteran of the Depths") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Merfolk Soldier"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature becomes tapped, you may put a +1/+1 counter on it."

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = MayEffect(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
        description = "Whenever this creature becomes tapped, you may put a +1/+1 counter on it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Daren Bader"
        flavorText = "In the backwaters of the Merrow Lanes lie stones scarred with tallies of countless generations, each representing a victory of merrow soldiers."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c36fe821-e9b1-453d-8e44-f8dce111a6de.jpg?1783942907"
    }
}
