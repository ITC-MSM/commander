package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Library Larcenist
 * {2}{U}
 * Creature — Merfolk Rogue
 * 1/2
 * Whenever this creature attacks, draw a card.
 */
val LibraryLarcenist = card("Library Larcenist") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 1
    toughness = 2
    oracleText = "Whenever this creature attacks, draw a card."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.DrawCards(1)
        description = "Whenever this creature attacks, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Mila Pesic"
        flavorText = "\"I specialize in missing manuscripts, pilfered poetry, and lifted letters.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb33529b-80bd-4f52-94cc-d8371c53ad75.jpg?1783930725"
        ruling(
            "2020-06-23",
            "You may cast spells and activate abilities after the card has been drawn but before " +
                "blockers are declared."
        )
    }
}
