package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Falkenrath Exterminator
 * {1}{R}
 * Creature — Vampire Archer
 * 1 / 1
 *
 * Whenever this creature deals combat damage to a player, put a +1/+1 counter on it.
 * {2}{R}: This creature deals damage to target creature equal to the number of +1/+1 counters on this creature.
 *
 * The damage amount is a live read of the Exterminator's own +1/+1 counters
 * ([DynamicAmounts.countersOnSelf]), so a counter added or removed between activation and
 * resolution changes how much damage is dealt.
 */
val FalkenrathExterminator = card("Falkenrath Exterminator") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Archer"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature deals combat damage to a player, put a +1/+1 counter on it.\n" +
        "{2}{R}: This creature deals damage to target creature equal to the number of +1/+1 counters on " +
        "this creature."

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(
            DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusOne),
            t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40e23909-7e08-4686-ae59-e18e7d4cfd3c.jpg?1783940685"
    }
}
