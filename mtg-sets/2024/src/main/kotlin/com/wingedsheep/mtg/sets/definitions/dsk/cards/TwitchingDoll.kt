package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Twitching Doll
 * {1}{G}
 * Artifact Creature — Spider Toy
 * 2/2
 * {T}: Add one mana of any color. Put a nest counter on this creature.
 * {T}, Sacrifice this creature: Create a 2/2 green Spider creature token with reach for each
 * counter on this creature. Activate only as a sorcery.
 */
val TwitchingDoll = card("Twitching Doll") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Spider Toy"
    oracleText = "{T}: Add one mana of any color. Put a nest counter on this creature.\n{T}, Sacrifice this creature: Create a 2/2 green Spider creature token with reach for each counter on this creature. Activate only as a sorcery."
    power = 2
    toughness = 2

    // {T}: Add one mana of any color. Put a nest counter on this creature.
    // This is still a mana ability (CR 605.1a): no target, and it could add mana.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            Effects.AddManaOfChoice(),
            Effects.AddCounters(Counters.NEST, 1, EffectTarget.Self)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {T}, Sacrifice this creature: Create a 2/2 green Spider creature token with reach for
    // each counter on this creature. Activate only as a sorcery.
    //
    // The sacrifice is paid as a cost, which wipes the doll's counters before the effect
    // resolves (CR 122.2). "for each counter on this creature" therefore reads the pre-cost
    // count as last-known information (CR 112.7a) — the engine snapshots the source's counters
    // at cost-payment time, read here via DynamicAmount.LastKnownSourceCounters over every
    // counter kind (CounterTypeFilter.Any). This mirrors Lost Isle Calling.
    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        timing = TimingRule.SorcerySpeed
        effect = Effects.CreateToken(
            count = DynamicAmounts.lastKnownSourceCounters(CounterTypeFilter.Any),
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Spider"),
            keywords = setOf(Keyword.REACH),
            imageUri = "https://cards.scryfall.io/normal/front/4/f/4f8852eb-1318-40c2-aa2a-8c0e830cca71.jpg?1726236714"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "201"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/416c025b-e40e-4d95-a774-ba3961f43808.jpg?1726286615"
    }
}
