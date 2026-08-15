package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Donatello, Turtle Techie
 * {3}{U}
 * Legendary Creature — Mutant Ninja Turtle
 * 3/4
 *
 * When Donatello enters, if you control an artifact, draw a card.
 */
val DonatelloTurtleTechie = card("Donatello, Turtle Techie") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "When Donatello enters, if you control an artifact, draw a card."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.ControlArtifact
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Ryan Valle"
        flavorText = "\"My son, for someone so intelligent, the obvious often eludes you.\"\n—Splinter"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/683cfef0-f164-4db8-b83f-79eb804e50ae.jpg?1771502569"
    }
}
