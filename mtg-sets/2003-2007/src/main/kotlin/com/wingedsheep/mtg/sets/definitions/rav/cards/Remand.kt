package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Remand — Ravnica: City of Guilds #63 (canonical printing)
 * {1}{U} · Instant
 *
 * Counter target spell. If that spell is countered this way, put it into its owner's hand instead
 * of into that player's graveyard.
 * Draw a card.
 *
 * The "instead of into that player's graveyard" clause is a [CounterDestination], not a bounce.
 * That distinction is the whole card: `ReturnSpellToOwnersHandEffect` is explicitly *not* a counter,
 * so modelling Remand with it would let it "counter" an uncounterable spell and would stop every
 * "whenever a spell is countered" trigger from seeing it. Both rulings below fall out of routing
 * through the counter path instead — an uncounterable spell is neither countered nor returned (you
 * still draw), and a flashback card's own exile replacement still wins over the hand.
 *
 * The draw is a separate clause and happens regardless of what the counter did, so it sits beside
 * the counter in a [Effects.Composite] rather than inside it.
 */
val Remand = card("Remand") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. If that spell is countered this way, put it into its " +
        "owner's hand instead of into that player's graveyard.\n" +
        "Draw a card."

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.Composite(
            listOf(
                Effects.CounterSpellToHand(),
                Effects.DrawCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Mark A. Nelson"
        flavorText = "\"Well, at least all of that arm-waving and arcane babbling you did was impressive.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/581f3780-c480-48c6-b15c-1618f2feccb9.jpg?1783943680"
        ruling(
            "2021-03-19",
            "Remand can target a spell that can't be countered. That spell won't be countered or " +
                "returned to its owner's hand, but you'll draw a card."
        )
        ruling(
            "2021-03-19",
            "If you target a card that was cast with flashback with Remand, the card will still be exiled."
        )
    }
}
