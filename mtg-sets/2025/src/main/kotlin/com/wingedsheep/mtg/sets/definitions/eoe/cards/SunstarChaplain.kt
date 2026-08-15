package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sunstar Chaplain
 * {1}{W}
 * Creature — Human Cleric
 * 3/2
 *
 * At the beginning of your end step, if you control two or more tapped creatures,
 * put a +1/+1 counter on target creature you control.
 * {2}, Remove a +1/+1 counter from a creature you control: Tap target artifact or creature.
 */
val SunstarChaplain = card("Sunstar Chaplain") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 3
    toughness = 2
    oracleText = "At the beginning of your end step, if you control two or more tapped creatures, " +
        "put a +1/+1 counter on target creature you control.\n" +
        "{2}, Remove a +1/+1 counter from a creature you control: Tap target artifact or creature."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.tapped()),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2)
        )
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
        description = "put a +1/+1 counter on target creature you control"
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.RemovePlusOnePlusOneCounters(GameObjectFilter.Creature.youControl(), 1)
        )
        val t = target(
            "target artifact or creature",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrArtifact))
        )
        effect = Effects.Tap(t)
        description = "Tap target artifact or creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "40"
        artist = "Valera Lutfullina"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83719626-3ff8-4566-9911-88212e753c69.jpg?1752946709"

        ruling(
            "2025-07-25",
            "Sunstar Chaplain's first ability will check as your end step starts to see if you " +
                "control two or more tapped creatures. If you don't, the ability won't trigger at all. " +
                "You won't be able to tap anything during your end step in time to have the ability " +
                "trigger. If you don't control two or more tapped creatures when the ability resolves, " +
                "the ability won't do anything."
        )
    }
}
