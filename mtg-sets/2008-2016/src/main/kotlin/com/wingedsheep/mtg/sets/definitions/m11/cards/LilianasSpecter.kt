package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Liliana's Specter
 * {1}{B}{B}
 * Creature — Specter
 * 2/1
 *
 * Flying
 * When this creature enters, each opponent discards a card.
 *
 * - The discard is [Patterns.Hand].eachOpponentDiscards — one per-opponent iteration that gathers
 *   that player's hand, has them choose a card, and moves it to their graveyard as a discard.
 */
val LilianasSpecter = card("Liliana's Specter") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Specter"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature enters, each opponent discards a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
        description = "When this creature enters, each opponent discards a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Vance Kovacs"
        flavorText = "\"The finest minions know what I need without me ever saying a thing.\"\n" +
            "—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33122581-39fd-44a0-b928-f73e39a0c0f1.jpg?1783941814"
    }
}
