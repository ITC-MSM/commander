package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Earth King's Lieutenant
 * {G}{W}
 * Creature — Human Soldier Ally
 * 1/1
 * Trample
 * When this creature enters, put a +1/+1 counter on each other Ally creature you control.
 * Whenever another Ally you control enters, put a +1/+1 counter on this creature.
 */
val EarthKingsLieutenant = card("Earth King's Lieutenant") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Soldier Ally"
    oracleText = "Trample\n" +
        "When this creature enters, put a +1/+1 counter on each other Ally creature you control.\n" +
        "Whenever another Ally you control enters, put a +1/+1 counter on this creature."
    power = 1
    toughness = 1
    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = AddCountersEffect(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = EffectTarget.GroupRef(
                GroupFilter(
                    baseFilter = GameObjectFilter.Creature.withSubtype(Subtype.ALLY).youControl(),
                    excludeSelf = true
                )
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ALLY).youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = AddCountersEffect(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            count = 1,
            target = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Nathaniel Himawan"
        flavorText = "\"You know well the cost of war.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/5/4533d155-5c56-41a5-9d76-2d1414ac47c9.jpg?1764121563"
    }
}
