package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Change of Fortune
 * {3}{R}
 * Sorcery
 *
 * Discard your hand, then draw a card for each card you've discarded this turn.
 *
 * The "then" is load-bearing: the discard runs first and feeds the same per-player
 * `CardsDiscardedThisTurnComponent` that [DynamicAmounts.cardsDiscardedThisTurn] reads, so the
 * cards discarded to this very spell are counted along with everything discarded earlier in the
 * turn — which is exactly the printed ruling. Sequencing inside a [Effects.Composite] is what
 * gets that ordering for free; the amount is evaluated when the draw executes, not when the
 * spell is put on the stack.
 */
val ChangeOfFortune = card("Change of Fortune") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Discard your hand, then draw a card for each card you've discarded this turn."

    spell {
        effect = Effects.Composite(
            listOf(
                Patterns.Hand.discardHand(),
                Effects.DrawCards(DynamicAmounts.cardsDiscardedThisTurn())
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Sam Guay"
        flavorText = "\"We cannot escape change, but with faith and fortitude, we can mold it to our liking.\"\n" +
            "—Marel, Dawnhart witch"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63ca5ce5-94e7-43a1-8f2b-f0a4532f617a.jpg?1783924841"
        ruling(
            "2021-11-19",
            "Change of Fortune counts cards you discarded this turn for any reason, not just cards you " +
                "discarded as part of the resolution of this effect."
        )
    }
}
