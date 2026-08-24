package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunt for Specimens — Strixhaven: School of Mages #73 (canonical printing)
 * {1}{B} · Sorcery
 *
 * Create a 1/1 black and green Pest creature token with "When this token dies, you gain 1 life."
 * Learn.
 *
 * The Pest is Strixhaven's signature Witherbloom token, so it lives in the predefined-token
 * registry (`PredefinedTokens.Pest`) rather than being spelled out inline: it is a *named* token
 * carrying its own triggered ability, which the inline `Effects.CreateToken` facade cannot
 * express, and roughly a dozen STX cards mint the identical body.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val HuntForSpecimens = card("Hunt for Specimens") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Create a 1/1 black and green Pest creature token with \"When this token dies, " +
        "you gain 1 life.\"\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        effect = Effects.CreatePest() then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8ff0f47f-75cb-42b0-ba4d-78522cad9861.jpg?1783927367"
    }
}
