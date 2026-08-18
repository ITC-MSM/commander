package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackOrBlockUnlessPay
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Myr Prototype — Mirrodin #214 (canonical printing)
 * {5} · Artifact Creature — Myr · 2/2
 *
 * At the beginning of your upkeep, put a +1/+1 counter on this creature.
 * This creature can't attack or block unless you pay {1} for each +1/+1 counter on it.
 *
 * The two lines are a single joke: every upkeep makes it bigger *and* dearer, so the price of
 * swinging is always exactly the bonus it has accumulated. That is why
 * [CantAttackOrBlockUnlessPay] takes a [com.wingedsheep.sdk.scripting.values.DynamicAmount]
 * instead of an `Int` — a tax frozen at the printed number would be free forever.
 *
 * The tax is a *cost of declaring* it (CR 508.1a / 509.1a), not a restriction: the engine prices
 * the declaration through `CombatTaxes` and pauses for the payment, so declining to pay un-declares
 * this creature and leaves the rest of the attack standing. Attacking and blocking are charged
 * separately — a turn cycle where it does both pays twice.
 */
val MyrPrototype = card("Myr Prototype") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Myr"
    power = 2
    toughness = 2
    oracleText = "At the beginning of your upkeep, put a +1/+1 counter on this creature.\n" +
        "This creature can't attack or block unless you pay {1} for each +1/+1 counter on it."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, put a +1/+1 counter on this creature."
    }

    staticAbility {
        ability = CantAttackOrBlockUnlessPay(
            amount = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.PLUS_ONE_PLUS_ONE))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Dave Dorman"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d929b28-c184-4e77-a40b-ee43b8a37d79.jpg?1783944510"
    }
}
