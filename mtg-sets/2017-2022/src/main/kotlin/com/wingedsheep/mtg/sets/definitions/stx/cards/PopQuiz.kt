package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pop Quiz — Strixhaven: School of Mages #49 (canonical printing)
 * {2}{U} · Instant
 *
 * Draw a card.
 * Learn.
 *
 * The order matters and is printed order: the draw happens *before* the Learn, so the freshly
 * drawn card is already in hand and is itself a legal thing to pitch to the Learn's discard.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val PopQuiz = card("Pop Quiz") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw a card.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        effect = Effects.DrawCards(1) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Matt Stewart"
        flavorText = "\"Today is hydromancy? I thought it was amplimancy! I studied for amplimancy!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d16892d8-9d10-45de-ab79-0e645c4b5588.jpg?1783927376"
    }
}
