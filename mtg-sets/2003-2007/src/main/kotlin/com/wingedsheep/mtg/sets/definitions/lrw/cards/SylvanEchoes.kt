package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sylvan Echoes
 * {G}
 * Enchantment
 * Whenever you clash and win, you may draw a card.
 *
 * The clash *payoff* rather than a clash source — it never clashes itself, it watches. Two things
 * that makes load-bearing, and both are in its ruling ("if you win a clash initiated by a spell or
 * ability an opponent controls, the ability will still trigger"):
 *
 *  - The trigger is [Triggers.WheneverYouClashAndWin], which watches the `ClashedEvent` emitted for
 *    *you as a participant*. An opponent's Adder-Staff Boggart makes you clash too, and if your
 *    revealed card is the bigger one you drew from their trigger.
 *  - The win is on the trigger, not inside the effect, because the printed wording is "clash **and
 *    win**" — losing the clash puts nothing on the stack at all.
 */
val SylvanEchoes = card("Sylvan Echoes") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Whenever you clash and win, you may draw a card."

    triggeredAbility {
        trigger = Triggers.WheneverYouClashAndWin
        effect = Effects.DrawCards(1)
        optional = true
        description = "you may draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Rebecca Guay"
        flavorText = "It takes a huntmaster's eye to discern the contours of mythical prey."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5b55fa6-4c94-4ab4-bc30-4fb8b1d3e38f.jpg?1783942856"
    }
}
