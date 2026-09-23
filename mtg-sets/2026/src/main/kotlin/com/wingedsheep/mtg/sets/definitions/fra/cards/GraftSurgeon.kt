package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val GraftSurgeon = card("Graft Surgeon") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "This creature enters with a +1/+1 counter on it.\n" +
        "When this creature dies, put its counters on up to one target creature you control."

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true
    ))

    triggeredAbility {
        trigger = Triggers.Dies
        target = TargetCreature(
            optional = true,
            filter = TargetFilter(GameObjectFilter.Creature.youControl())
        )
        effect = Effects.MoveAllLastKnownCounters(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Bram Sels"
        flavorText = "When medical supplies are limited, her own cuttings will suffice."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32a7a905-11bf-4b66-a28e-1066a0e372b8.jpg?1789644811"
        inBooster = false
    }
}
