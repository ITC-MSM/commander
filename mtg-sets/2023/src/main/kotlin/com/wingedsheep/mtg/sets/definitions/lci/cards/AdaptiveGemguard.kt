package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Adaptive Gemguard
 * {3}{W}
 * Artifact Creature — Gnome
 * 3/3
 * Tap two untapped artifacts and/or creatures you control: Put a +1/+1 counter on this creature.
 * Activate only as a sorcery.
 *
 * The activation cost is "tap two untapped artifacts and/or creatures you control" (no "other"
 * restriction — Adaptive Gemguard itself can be tapped as one of the two).  This is modelled with
 * [Costs.TapPermanents] (the atomic `CostAtom.TapPermanents` primitive), which is exactly the right
 * shape for "tap N untapped permanents matching a filter as an ability cost".
 */
val AdaptiveGemguard = card("Adaptive Gemguard") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Gnome"
    power = 3
    toughness = 3
    oracleText = "Tap two untapped artifacts and/or creatures you control: Put a +1/+1 counter on this creature. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Artifact or GameObjectFilter.Creature,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Anthony Devine"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83c91e24-b7e8-4040-80cb-d1b375002c10.jpg?1782694608"
    }
}
