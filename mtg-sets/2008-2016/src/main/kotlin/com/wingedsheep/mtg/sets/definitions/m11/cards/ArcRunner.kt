package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Arc Runner
 * {2}{R}
 * Creature — Elemental Ox
 * 5/1
 *
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 * At the beginning of the end step, sacrifice this creature.
 */
val ArcRunner = card("Arc Runner") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Ox"
    power = 5
    toughness = 1
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "At the beginning of the end step, sacrifice this creature."

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = SacrificeSelfEffect
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Nils Hamm"
        flavorText = "The storms of the wastelands form quickly and hit hard. Few have anything to do with rain."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32014715-7faa-412a-b8b4-751102e6b8a3.jpg?1783941809"
    }
}
