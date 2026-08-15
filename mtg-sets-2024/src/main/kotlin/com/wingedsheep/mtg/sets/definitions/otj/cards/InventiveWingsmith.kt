package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inventive Wingsmith
 * {2}{W}
 * Creature — Dwarf Artificer
 * 2/4
 *
 * At the beginning of your end step, if you haven't cast a spell from your hand this turn and
 * this creature doesn't have a flying counter on it, put a flying counter on it.
 *
 * Intervening-if (CR 603.4, Scryfall ruling 2024-04-12): the end-step ability checks both
 * clauses — "haven't cast a spell from your hand this turn" and "no flying counter" — when it
 * would trigger and again as it resolves. If either is false at either check, the ability does
 * nothing. The flying counter is the "flying" keyword counter, which grants flying via the
 * keyword-counter projection (StateProjector.KEYWORD_COUNTER_MAP).
 */
val InventiveWingsmith = card("Inventive Wingsmith") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Artificer"
    power = 2
    toughness = 4
    oracleText = "At the beginning of your end step, if you haven't cast a spell from your hand " +
        "this turn and this creature doesn't have a flying counter on it, put a flying counter on it."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.All(
            Conditions.Not(Conditions.YouCastSpellsThisTurn(1, fromZone = Zone.HAND)),
            Conditions.Not(Conditions.SourceHasCounter(CounterTypeFilter.Named(Counters.FLYING))),
        )
        effect = Effects.AddCounters(Counters.FLYING, 1, EffectTarget.Self)
        description = "At the beginning of your end step, if you haven't cast a spell from your hand " +
            "this turn and this creature doesn't have a flying counter on it, put a flying counter on it."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "David Astruga"
        flavorText = "\"We must forge for ourselves the gifts that nature neglected to give us.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6b36bb3-dacc-44f6-adcd-2c2d65513d8c.jpg?1712355293"

        ruling("2024-04-12", "At the beginning of your end step, Inventive Wingsmith's ability will check to see if you've cast a spell from your hand this turn and if Inventive Wingsmith has a flying counter on it. If either is true, the ability won't trigger at all. If the ability does trigger, it will check again as it tries to resolve. If either is true as the ability tries to resolve, the ability won't do anything.")
    }
}
