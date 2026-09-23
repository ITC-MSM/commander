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

val WayOfTheNecromancer = card("Way of the Necromancer") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Necromancer enters, empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Whenever a creature you control dies, put a loyalty counter on each planeswalker you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(2)
    }

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Planeswalker.youControl()),
            Effects.AddCounters(Counters.LOYALTY, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Pauline Voss"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0ff9689-ea49-4fff-b37c-4abbaeb0f73d.jpg?1789729526"
        inBooster = false
    }
}
