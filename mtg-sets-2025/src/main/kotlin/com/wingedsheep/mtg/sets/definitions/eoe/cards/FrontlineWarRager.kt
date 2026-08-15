package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Frontline War-Rager
 * {2}{R}
 * Creature — Kavu Soldier
 * 2/3
 *
 * At the beginning of your end step, if you control two or more tapped creatures,
 * put a +1/+1 counter on this creature.
 */
val FrontlineWarRager = card("Frontline War-Rager") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu Soldier"
    power = 2
    toughness = 3
    oracleText = "At the beginning of your end step, if you control two or more tapped creatures, " +
        "put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.tapped()),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Jason Rainville"
        flavorText = "The Kav have more than ideology or profit on the line. They fight for a home."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa232943-818b-4944-be60-2d80c806bf62.jpg?1752947094"

        ruling(
            "2025-07-25",
            "Frontline War-Rager's ability will check as your end step starts to see if you control " +
                "two or more tapped creatures. If you don't, the ability won't trigger at all. You won't " +
                "be able to tap anything during your end step in time to have the ability trigger. If you " +
                "don't control two or more tapped creatures when the ability resolves, the ability won't " +
                "do anything."
        )
    }
}
