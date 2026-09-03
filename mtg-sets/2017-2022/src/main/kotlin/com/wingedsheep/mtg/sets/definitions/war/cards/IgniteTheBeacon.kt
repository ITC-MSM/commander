package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Ignite the Beacon — War of the Spark #18 (canonical printing)
 * {4}{W}
 * Instant
 * Search your library for up to two planeswalker cards, reveal them, put them into your hand,
 * then shuffle.
 *
 * Straight [Patterns.Library.searchLibrary]: "up to two" is the recipe's `count` (the selection
 * is always an up-to, so an empty library or a stubborn player finds nothing), "reveal them" is
 * `reveal = true` on the move to hand, and "then shuffle" is the default `shuffleAfter`. The
 * recipe also emits the library-searched event, which is what "search" means for the triggers
 * that watch for it.
 */
val IgniteTheBeacon = card("Ignite the Beacon") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Search your library for up to two planeswalker cards, reveal them, put them " +
        "into your hand, then shuffle."

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Planeswalker,
            count = 2,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Slawomir Maniak"
        flavorText = "\"If you can't save yourself, you fight to give someone else a chance.\"\n—Ajani Goldmane"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e11d6f9-0b80-4b19-ab96-23f80b66b409.jpg"
    }
}
