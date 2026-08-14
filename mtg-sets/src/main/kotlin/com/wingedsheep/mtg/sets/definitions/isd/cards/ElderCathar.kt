package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Elder Cathar
 * {2}{W}
 * Creature — Human Soldier
 * 2/2
 * When this creature dies, put a +1/+1 counter on target creature you control.
 * If that creature is a Human, put two +1/+1 counters on it instead.
 */
val ElderCathar = card("Elder Cathar") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText =
        "When this creature dies, put a +1/+1 counter on target creature you control. " +
            "If that creature is a Human, put two +1/+1 counters on it instead."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.HUMAN),
            ),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature),
            elseEffect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Chris Rahn"
        flavorText =
            "\"My greatest hope is that you will surpass me in every way, " +
                "my student. Then I can consider my life's work complete.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/c/2/c21b9e51-fecd-4f9a-9354-a6dc1613feb3.jpg?1562837015"
    }
}
