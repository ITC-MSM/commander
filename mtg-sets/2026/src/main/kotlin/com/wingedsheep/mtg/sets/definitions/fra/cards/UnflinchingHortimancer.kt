package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val UnflinchingHortimancer = card("Unflinching Hortimancer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "Ward {1} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {1}.)\nWhenever you gain life, put a +1/+1 counter on this creature."
    power = 2
    toughness = 1

    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{1}")))

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Manuel Castañón"
        flavorText = "Battlefield medicine is as much about timing as it is healing. A perfect remedy is worthless if it comes too late."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63f82985-c9c2-4d0a-ac4f-560166bebd9f.jpg?1789556741"
        inBooster = false
    }
}
