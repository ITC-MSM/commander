package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trigon of Corruption — Scars of Mirrodin #213
 * {4} · Artifact
 *
 * This artifact enters with three charge counters on it.
 * {B}{B}, {T}: Put a charge counter on this artifact.
 * {2}, {T}, Remove a charge counter from this artifact: Put a -1/-1 counter on target creature.
 *
 * The charge counters are a battery, not a clock: [EntersWithCounters] loads three at entry, the
 * first ability recharges one at a time, and the second spends one. Both abilities tap, so the
 * Trigon fires at most once a turn and recharging costs it that turn's shot.
 *
 * The removal is [Costs.RemoveCounterFromSelf] — a self-scoped counter atom rather than a
 * filtered "from among permanents you control" one, so there is nothing for the player to choose
 * and the ability is simply unactivatable at zero charge counters.
 */
val TrigonOfCorruption = card("Trigon of Corruption") {
    manaCost = "{4}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "This artifact enters with three charge counters on it.\n" +
        "{B}{B}, {T}: Put a charge counter on this artifact.\n" +
        "{2}, {T}, Remove a charge counter from this artifact: Put a -1/-1 counter on target creature."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 3,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{B}"), Costs.Tap)
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE)
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26e215e0-836c-4b37-8f9a-9093a535bff1.jpg?1783941694"
    }
}
