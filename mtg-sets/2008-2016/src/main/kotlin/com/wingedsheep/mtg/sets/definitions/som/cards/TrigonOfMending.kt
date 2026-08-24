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
 * Trigon of Mending
 * {2}
 * Artifact
 *
 * This artifact enters with three charge counters on it.
 * {W}{W}, {T}: Put a charge counter on this artifact.
 * {2}, {T}, Remove a charge counter from this artifact: Target player gains 3 life.
 *
 * Both halves share the tap symbol, so the Trigon either recharges or fires in a given turn cycle,
 * never both. The removal is part of the *cost*, so an activation that gets countered on the stack
 * does not refund the counter.
 */
val TrigonOfMending = card("Trigon of Mending") {
    manaCost = "{2}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "This artifact enters with three charge counters on it.\n" +
        "{W}{W}, {T}: Put a charge counter on this artifact.\n" +
        "{2}, {T}, Remove a charge counter from this artifact: Target player gains 3 life."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 3,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{W}{W}"),
            Costs.Tap
        )
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        description = "{W}{W}, {T}: Put a charge counter on this artifact."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE, 1)
        )
        val player = target("target player", Targets.Player)
        effect = Effects.GainLife(3, player)
        description = "{2}, {T}, Remove a charge counter from this artifact: Target player gains 3 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/241142e0-3a79-4bce-8535-18ae7e392f5e.jpg?1783941693"
    }
}
