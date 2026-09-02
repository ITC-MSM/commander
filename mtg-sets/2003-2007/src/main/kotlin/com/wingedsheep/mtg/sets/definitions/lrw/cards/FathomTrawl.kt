package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fathom Trawl
 * {3}{U}{U}
 * Sorcery
 * Reveal cards from the top of your library until you reveal three nonland cards. Put the nonland
 * cards revealed this way into your hand, then put the rest of the revealed cards on the bottom of
 * your library in any order.
 *
 * `Patterns.Library.revealUntilMatchToHand` at `count = 3`: the gather stops once three nonland
 * cards have been revealed, then the same nonland partition splits the revealed pile — matches to
 * hand, the lands revealed alongside them to the bottom. "In any order" is
 * [CardOrder.ControllerChooses], not the pattern's default random order.
 *
 * The 2007-10-01 ruling — fewer than three nonland cards in the library reveals the whole library,
 * takes every nonland card, and bottoms the rest — is the gather's own empty-library behaviour, not
 * a separate branch.
 */
val FathomTrawl = card("Fathom Trawl") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Reveal cards from the top of your library until you reveal three nonland cards. " +
        "Put the nonland cards revealed this way into your hand, then put the rest of the revealed " +
        "cards on the bottom of your library in any order."

    spell {
        effect = Patterns.Library.revealUntilMatchToHand(
            filter = GameObjectFilter.Nonland,
            restOrder = CardOrder.ControllerChooses,
            count = DynamicAmount.Fixed(3)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "65"
        artist = "Paul Chadwick"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd9c0bd7-f8a9-4d13-9a06-acc8bfb3d68b.jpg?1783942903"
        ruling(
            "2007-10-01",
            "If there are fewer than three nonland cards in your library, you will reveal your " +
                "entire library, put all nonland cards into your hand, and put the rest back in any order."
        )
    }
}
