package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dangerous Wager
 * {1}{R}
 * Instant
 *
 * Discard your hand, then draw two cards.
 *
 * The discard is [Patterns.Hand.discardHand]'s gather/move pair, kept as its own nested composite:
 * `then` would splice that pair into the outer list and flatten the sentence's two clauses into
 * three, so the two halves are joined with an explicit [Effects.Composite].
 */
val DangerousWager = card("Dangerous Wager") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Discard your hand, then draw two cards."

    spell {
        effect = Effects.Composite(
            Patterns.Hand.discardHand(),
            Effects.DrawCards(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Drew Baker"
        flavorText = "\"C'mon friend, take a turn tossing the knucklebones. What've you got to lose?\"\n—Tobias, trader of Erdwal"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/636c4042-703f-4548-9a0f-cb550c468bf9.jpg?1783940687"
    }
}
