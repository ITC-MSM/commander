package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val WayOfTheMentor = card("Way of the Mentor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Mentor enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Whenever you gain life, put a loyalty counter on each planeswalker you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Planeswalker.youControl()),
            Effects.AddCounters(Counters.LOYALTY, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Danny Schwartz"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a59d5b1-12d6-486b-bd29-ca371359addd.jpg?1789729554"
        inBooster = false
    }
}
