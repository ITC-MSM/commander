package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Mikey & Leo, Chaos & Order
 * {G/W}{G/W}
 * Legendary Creature — Mutant Ninja Turtle
 * 2/2
 *
 * Whenever you put a counter on a creature you control, draw a card.
 * This ability triggers only once each turn.
 */
val MikeyAndLeoChaosAndOrder = card("Mikey & Leo, Chaos & Order") {
    manaCost = "{G/W}{G/W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "Whenever you put a counter on a creature you control, draw a card. This ability triggers only once each turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Creature.youControl(),
            counterType = Counters.ANY,
            firstTimeEachTurn = false,
        )
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "158"
        artist = "Jason Rainville"
        flavorText = "Their strength hailed not from what they shared, but from how their differences complemented one another."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cfeb2b4-937c-4bcc-bb03-467cc0effba2.jpg?1769006353"
    }
}
