package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Warden of the Inner Sky
 * {W}
 * Creature — Human Soldier
 * 1/2
 *
 * As long as this creature has three or more counters on it, it has flying and vigilance.
 * Tap three untapped artifacts and/or creatures you control: Put a +1/+1 counter on this
 * creature. Scry 1. Activate only as a sorcery.
 *
 * The counter-threshold keyword grant is modeled as two conditional [staticAbility] blocks gated
 * on [Conditions.SourceCounterCountAtLeast] (same shape as Skyknight Squire) — one granting
 * FLYING, one granting VIGILANCE. Both apply only while the source carries 3+ counters, projected
 * through the layer system.
 *
 * The oracle text counts counters of *any* kind, so the threshold is gated on
 * [CounterTypeFilter.Any], which sums every counter kind on the source — a stun counter placed by
 * an opponent counts toward the three just like the +1/+1 counters from its own ability.
 *
 * The activation cost reuses the [Costs.TapPermanents] primitive (same shape as Sunshot Militia,
 * but count = 3): tap exactly three untapped artifacts and/or creatures you control (no "other"
 * restriction — Warden itself may be one of the three). The effect puts a +1/+1 counter on Warden
 * then scries 1, at sorcery speed.
 */
val WardenOfTheInnerSky = card("Warden of the Inner Sky") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "As long as this creature has three or more counters on it, it has flying and " +
        "vigilance.\n" +
        "Tap three untapped artifacts and/or creatures you control: Put a +1/+1 counter on this " +
        "creature. Scry 1. Activate only as a sorcery."

    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 3)
        ability = GrantKeyword(Keyword.FLYING, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 3)
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.Self)
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 3,
            filter = GameObjectFilter.Artifact or GameObjectFilter.Creature,
        )
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            Effects.Scry(1),
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "43"
        artist = "Raoul Vitale"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/549fd992-ed37-431d-97cb-9ca017db1d47.jpg?1782694577"
    }
}
