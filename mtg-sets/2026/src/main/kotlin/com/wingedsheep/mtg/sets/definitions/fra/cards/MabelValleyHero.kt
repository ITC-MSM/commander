package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val MabelValleyHero = card("Mabel, Valley Hero") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Mouse Soldier"
    power = 1
    toughness = 2
    oracleText = "Whenever Mabel or another creature you control enters, put a +1/+1 counter on target creature that entered this turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        val t = target(
            "target creature that entered this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.enteredThisTurn()))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
        description = "Whenever Mabel or another creature you control enters, put a +1/+1 counter on target creature that entered this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "274"
        artist = "Matt Stewart"
        flavorText = "Mabel had learned that mother, adventurer, and protector were not all that far apart."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47abea4b-9848-48aa-bc1b-f04f4799e920.jpg?1789385857"
        inBooster = false
    }
}
