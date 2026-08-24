package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Necrogen Censer
 * {3}
 * Artifact
 *
 * This artifact enters with two charge counters on it.
 * {T}, Remove a charge counter from this artifact: Target player loses 2 life.
 */
val NecrogenCenser = card("Necrogen Censer") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "This artifact enters with two charge counters on it.\n" +
        "{T}, Remove a charge counter from this artifact: Target player loses 2 life."

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 2,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.CHARGE)
        )
        val victim = target("target player", Targets.Player)
        effect = Effects.LoseLife(2, victim)
        description = "{T}, Remove a charge counter from this artifact: Target player loses 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Pete Venters"
        flavorText = "Moriok necromancers actively spread necrogen, encouraging the transformation from Mirran to nim."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f707119-ede9-4697-b723-d6cea96e6f2b.jpg?1783941702"
    }
}
