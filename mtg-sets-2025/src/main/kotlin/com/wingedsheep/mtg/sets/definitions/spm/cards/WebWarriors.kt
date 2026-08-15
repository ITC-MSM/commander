package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Web-Warriors
 * {4}{G/W}
 * Creature — Spider Hero
 * 4/3
 * When this creature enters, put a +1/+1 counter on each other creature you control.
 */
val WebWarriors = card("Web-Warriors") {
    manaCost = "{4}{G/W}"
    colorIdentity = "WG"
    typeLine = "Creature — Spider Hero"
    oracleText = "When this creature enters, put a +1/+1 counter on each other creature you control."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Thanh Tuấn"
        flavorText = "Together, this group of amazing friends joined forces to mend the severed skeins across time and space."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/741f5373-b51a-421a-9a74-326f0575c99b.jpg?1757377967"
    }
}
