package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Celebrity Fencer
 * {3}{W}
 * Creature — Elf Druid
 * 3 / 2
 * Alliance — Whenever another creature you control enters, put a +1/+1 counter on this creature.
 *
 * "Alliance" is a pure ability word, so this is the plain [Triggers.OtherCreatureEnters] (OTHER
 * binding over creatures you control); the ability word lives only in the printed text.
 */
val CelebrityFencer = card("Celebrity Fencer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elf Druid"
    oracleText = "Alliance — Whenever another creature you control enters, put a +1/+1 counter on this creature."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Alliance — Whenever another creature you control enters, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Inka Schulz"
        flavorText = "After a brief but memorable incident at her first performance, she was never heckled again."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5afb5c5c-06e0-4b11-ad07-aef7be6e2cd4.jpg?1783923161"
    }
}
