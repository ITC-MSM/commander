package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * EPF Point Squad
 * {1}{R/W}{R/W}
 * Creature — Human Soldier
 * 2/1
 *
 * Alliance — Whenever another creature you control enters, put a
 * +1/+1 counter on this creature.
 */
val EpfPointSquad = card("EPF Point Squad") {
    manaCost = "{1}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Creature — Human Soldier"
    oracleText = "Alliance — Whenever another creature you control enters, put a +1/+1 counter on this creature."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Alliance — Whenever another creature you control enters, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Leanna Crossan"
        flavorText = "Not all humans fear the unknown. The Earth Protection Force specializes in teaching the unknown to fear them instead."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/faab52c0-ce79-40af-a156-b193a62d439e.jpg?1771502756"
    }
}
