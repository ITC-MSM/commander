package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crocodile of the Crossing
 * {3}{G}
 * Creature — Crocodile
 * 5/4
 * Haste
 * When this creature enters, put a -1/-1 counter on target creature you control.
 */
val CrocodileOfTheCrossing = card("Crocodile of the Crossing") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Crocodile"
    oracleText = "Haste\n" +
            "When this creature enters, put a -1/-1 counter on target creature you control."
    power = 5
    toughness = 4

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, creature)
        description = "When this creature enters, put a -1/-1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "162"
        artist = "Kev Walker"
        flavorText = "\"Everything in the trial has teeth. You will overcome them, or you will feed them.\"\n—Rhonas, god of strength"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f64ca9c-99c4-45d4-bd21-3ede61702250.jpg?1783936477"
    }
}
