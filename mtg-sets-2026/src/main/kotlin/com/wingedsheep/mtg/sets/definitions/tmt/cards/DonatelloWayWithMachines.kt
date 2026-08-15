package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Donatello, Way with Machines
 * {2}{U}
 * Legendary Creature — Mutant Ninja Turtle
 * 1/2
 *
 * Flying
 * Whenever an artifact you control enters, put a +1/+1 counter on Donatello.
 */
val DonatelloWayWithMachines = card("Donatello, Way with Machines") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "Flying\nWhenever an artifact you control enters, put a +1/+1 counter on Donatello."
    power = 1
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Artifact.youControl(),
                to = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever an artifact you control enters, put a +1/+1 counter on Donatello."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Svetlin Velinov"
        flavorText = "\"Unfortunately, the lowly turtle has been saddled by society with the stereotype of being velocity challenged.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3a5b63a-3263-4f06-a643-808c38f64c77.jpg?1771502576"
    }
}
