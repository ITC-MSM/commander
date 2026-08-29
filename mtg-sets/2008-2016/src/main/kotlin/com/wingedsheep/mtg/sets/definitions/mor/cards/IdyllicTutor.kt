package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Idyllic Tutor
 * {2}{W}
 * Sorcery
 *
 * Search your library for an enchantment card, reveal it, put it into your hand, then shuffle.
 *
 * The whole sentence is one [Patterns.Library.searchLibrary] recipe: its defaults already gather
 * from the library, select up to one card, move the found card to hand, shuffle, and emit the
 * `LibrarySearchedEvent` (CR 701.23). Only `reveal = true` is spelled out — restating the pipeline
 * by hand is how a corpus card once silently lost `revealed`.
 */
val IdyllicTutor = card("Idyllic Tutor") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Search your library for an enchantment card, reveal it, put it into your hand, then shuffle."

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Enchantment,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Howard Lyon"
        flavorText = "\"If one's life is blessed, solutions to all life's problems will appear at the right " +
            "moment.\"\n—*The Book of Kith and Kin*"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a5e7a59-7322-46eb-9903-00131234b310.jpg"
    }
}
