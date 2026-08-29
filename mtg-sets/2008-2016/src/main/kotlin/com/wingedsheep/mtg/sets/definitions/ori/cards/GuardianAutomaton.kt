package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Guardian Automaton
 * {4}
 * Artifact Creature — Construct
 * 3/3
 *
 * When this creature dies, you gain 3 life.
 */
val GuardianAutomaton = card("Guardian Automaton") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    oracleText = "When this creature dies, you gain 3 life."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Vincent Proce"
        flavorText = "The wealthy in the city of Ghirapur outfit their lives with grand machines, entrusting even their children to filigree and gears."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e8916b7-f5e4-4fae-8db8-9859d69212ec.jpg"
    }
}
