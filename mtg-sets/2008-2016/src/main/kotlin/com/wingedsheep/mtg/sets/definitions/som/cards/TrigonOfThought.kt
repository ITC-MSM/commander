package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trigon of Thought — Scars of Mirrodin #217
 * {5} · Artifact
 *
 * This artifact enters with three charge counters on it.
 * {U}{U}, {T}: Put a charge counter on this artifact.
 * {2}, {T}, Remove a charge counter from this artifact: Draw a card.
 *
 * The counters it enters with are a replacement effect (CR 614.1c), not a trigger — nothing ever
 * sees the Trigon on the battlefield without them. Recharging and spending are two activated
 * abilities that each tap, so a single turn does one or the other.
 */
val TrigonOfThought = card("Trigon of Thought") {
    manaCost = "{5}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "This artifact enters with three charge counters on it.\n" +
        "{U}{U}, {T}: Put a charge counter on this artifact.\n" +
        "{2}, {T}, Remove a charge counter from this artifact: Draw a card."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 3,
            selfOnly = true,
        )
    )

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{U}"), Costs.Tap)
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE),
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Mike Bierek"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8da37ba-52e3-417e-8d7b-6c3e060552a4.jpg?1783941692"
    }
}
