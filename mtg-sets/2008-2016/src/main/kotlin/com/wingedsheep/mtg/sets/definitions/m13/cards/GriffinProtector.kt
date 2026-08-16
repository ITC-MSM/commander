package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Griffin Protector
 * {3}{W}
 * Creature — Griffin
 * 2/3
 * Flying
 * Whenever another creature you control enters, this creature gets +1/+1 until end of turn.
 */
val GriffinProtector = card("Griffin Protector") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    oracleText = "Flying\nWhenever another creature you control enters, this creature gets +1/+1 until end of turn."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Christopher Moeller"
        flavorText = "The drums of war stir the hearts of all who fight for righteousness."
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddae4f7a-525c-4306-81b5-b0991840a11e.jpg?1783940518"
    }
}
