package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Sarkhan's Triumph
 * {2}{R}
 * Instant
 *
 * Search your library for a Dragon creature card, reveal it, put it into your hand, then shuffle.
 *
 * The whole sentence is one recipe: [Patterns.Library.searchLibrary] gathers the matching cards,
 * lets the caster take up to one (CR 701.23b — finding a card is never mandatory), moves it to hand
 * revealed, shuffles, and emits the "a player searched their library" event. "Dragon creature card"
 * is a creature *and* a subtype, so the filter carries both predicates.
 */
val SarkhansTriumph = card("Sarkhan's Triumph") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Search your library for a Dragon creature card, reveal it, put it into your hand, then shuffle."

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.withSubtype("Dragon"),
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
            shuffleAfter = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "154"
        artist = "Chris Rahn"
        flavorText = "Sarkhan gazed on the world around him, the dragons sweeping through its skies, and joy kindled like a fire in his soul."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9a0ff4d-060e-41dc-8b79-1fa42838adf2.jpg?1783938586"
    }
}
