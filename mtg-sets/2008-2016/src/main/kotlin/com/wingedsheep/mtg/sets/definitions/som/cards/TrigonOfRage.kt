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
 * Trigon of Rage
 * {2}
 * Artifact
 *
 * This artifact enters with three charge counters on it.
 * {R}{R}, {T}: Put a charge counter on this artifact.
 * {2}, {T}, Remove a charge counter from this artifact: Target creature gets +3/+0 until end of turn.
 *
 * The printed "enters with three charge counters" line is an [EntersWithCounters] replacement
 * effect (CR 121.6), not a trigger; the counters are there as the artifact arrives. Both
 * activations spend or feed that same self-scoped pool.
 */
val TrigonOfRage = card("Trigon of Rage") {
    manaCost = "{2}"
    colorIdentity = "R"
    typeLine = "Artifact"
    oracleText = "This artifact enters with three charge counters on it.\n" +
        "{R}{R}, {T}: Put a charge counter on this artifact.\n" +
        "{2}, {T}, Remove a charge counter from this artifact: Target creature gets +3/+0 until end of turn."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 3,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}{R}"), Costs.Tap)
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        description = "{R}{R}, {T}: Put a charge counter on this artifact."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE)
        )
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 0, t)
        description = "{2}, {T}, Remove a charge counter from this artifact: " +
            "Target creature gets +3/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "216"
        artist = "Marc Simonetti"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/1135f3b7-8c6b-47ff-b895-b7127836b0bf.jpg?1783941693"
    }
}
