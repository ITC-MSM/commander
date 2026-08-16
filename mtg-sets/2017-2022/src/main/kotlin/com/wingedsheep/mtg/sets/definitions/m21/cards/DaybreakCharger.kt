package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Daybreak Charger
 * {1}{W}
 * Creature — Unicorn
 * 3/1
 * When this creature enters, target creature gets +2/+0 until end of turn.
 */
val DaybreakCharger = card("Daybreak Charger") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    power = 3
    toughness = 1
    oracleText = "When this creature enters, target creature gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 0, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Forrest Imel"
        flavorText = "It's often mistaken for the coming dawn as it gallops across the horizon."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff87a671-054f-4357-8a62-450d36559a1b.jpg?1783930743"
    }
}
