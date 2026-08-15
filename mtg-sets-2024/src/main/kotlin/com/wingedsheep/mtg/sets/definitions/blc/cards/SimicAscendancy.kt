package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Simic Ascendancy
 * {G}{U}
 * Enchantment
 *
 * {1}{G}{U}: Put a +1/+1 counter on target creature you control.
 * Whenever one or more +1/+1 counters are put on a creature you control,
 *   put that many growth counters on Simic Ascendancy.
 * At the beginning of your upkeep, if Simic Ascendancy has twenty or more
 *   growth counters on it, you win the game.
 */
val SimicAscendancy = card("Simic Ascendancy") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Enchantment"
    oracleText = "{1}{G}{U}: Put a +1/+1 counter on target creature you control.\n" +
        "Whenever one or more +1/+1 counters are put on a creature you control, " +
        "put that many growth counters on Simic Ascendancy.\n" +
        "At the beginning of your upkeep, if Simic Ascendancy has twenty or more " +
        "growth counters on it, you win the game."

    activatedAbility {
        cost = Costs.Mana("{1}{G}{U}")
        val creature = target("creature", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    triggeredAbility {
        trigger = Triggers.PlusOneCountersPlacedOnYourCreature
        effect = Effects.AddDynamicCounters(
            counterType = Counters.GROWTH,
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_COUNTERS_PLACED_AMOUNT),
            target = EffectTarget.Self
        )
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        triggerCondition = Compare(
            DynamicAmount.EntityProperty(
                EntityReference.Source,
                EntityNumericProperty.CounterCount(CounterTypeFilter.Named(Counters.GROWTH))
            ),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(20)
        )
        effect = Effects.WinGame(
            target = EffectTarget.Controller,
            message = "Simic Ascendancy reached 20 growth counters."
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/9300abb9-2750-4de8-8c1d-dbae48a86037.jpg?1721429509"

        ruling("2019-01-25", "An ability that triggers when counters are put on a permanent will trigger if that permanent somehow enters the battlefield with those counters.")
        ruling("2019-01-25", "If Simic Ascendancy doesn't have twenty or more growth counters on it as your upkeep begins, its last ability won't trigger. You can't take any actions during your turn before your upkeep begins.")
        ruling("2019-01-25", "If the last ability does trigger, but counters are removed from Simic Ascendancy so it has fewer than twenty remaining on it, you won't win the game.")
        ruling("2019-01-25", "If the last ability does trigger, but Simic Ascendancy leaves the battlefield, use the number of counters it had on it immediately before it left the battlefield to determine whether you win the game.")
    }
}
