package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Markov Dreadknight (Shadows over Innistrad #122)
 * {3}{B}{B}
 * Creature — Vampire Knight
 * 3 / 3
 *
 * Flying
 * {2}{B}, Discard a card: Put two +1/+1 counters on this creature.
 *
 * The discard is part of the cost, so it happens on activation and can't be responded to
 * separately — which is what makes this a madness enabler as well as a mana sink.
 */
val MarkovDreadknight = card("Markov Dreadknight") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{2}{B}, Discard a card: Put two +1/+1 counters on this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.DiscardCard)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "Darek Zabrocki"
        flavorText = "The destruction of Markov Manor made the surviving members of the bloodline more dangerous."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa3ed9d2-9053-4583-a226-0ab49bbdab6e.jpg?1783937770"
    }
}
