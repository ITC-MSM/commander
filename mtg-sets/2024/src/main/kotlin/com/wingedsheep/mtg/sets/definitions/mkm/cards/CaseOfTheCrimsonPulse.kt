package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedTriggeredAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity

/**
 * Case of the Crimson Pulse — Murders at Karlov Manor #114
 * {2}{R} · Enchantment — Case · Rare
 *
 * When this Case enters, discard a card, then draw two cards.
 * To solve — You have no cards in hand.
 * Solved — At the beginning of your upkeep, discard your hand, then draw two cards.
 *
 * Both halves are "discard, *then* draw", in that order, and both are unconditional on the discard
 * finding anything: with an empty hand you discard nothing and still draw two. That falls out of
 * composing the two effects rather than gating the draw on the discard — the printed ruling says
 * exactly this for both abilities.
 *
 * "You have no cards in hand" is a hand-size check, not a discard tracker, so anything that empties
 * your hand solves it; the enters trigger's own draw-two usually means it takes a turn cycle.
 */
val CaseOfTheCrimsonPulse = card("Case of the Crimson Pulse") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, discard a card, then draw two cards.\n" +
        "To solve — You have no cards in hand. (If unsolved, solve at the beginning of your end " +
        "step.)\n" +
        "Solved — At the beginning of your upkeep, discard your hand, then draw two cards."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Effects.Discard(1),
            Effects.DrawCards(2)
        )
        description = "When this Case enters, discard a card, then draw two cards."
    }

    toSolve(Conditions.CardsInHandAtMost(0))

    solvedTriggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Patterns.Hand.discardHand(),
            Effects.DrawCards(2)
        )
        description = "Solved — At the beginning of your upkeep, discard your hand, then draw two cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "114"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb18b1de-bc08-4522-b891-6117a8271534.jpg?1783912887"

        ruling(
            "2024-02-09",
            "If you have no cards in hand as Case of the Crimson Pulse's first ability resolves, " +
                "you won't discard a card, but you will still draw two cards. Similarly, if you " +
                "have no cards in hand when Case of the Crimson Pulse's last ability resolves, you " +
                "won't discard any cards, but you'll still draw two cards."
        )
    }
}
