package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Nagging Thoughts (Shadows over Innistrad #74)
 * {1}{U}
 * Sorcery
 *
 * Look at the top two cards of your library. Put one of them into your hand and the other into your graveyard.
 * Madness {1}{U} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)
 *
 * The dig is the standard [Patterns.Library.lookAtTopAndKeep] recipe — gather the top two, keep
 * exactly one (the default hand destination), rest to the graveyard (the default remainder
 * destination). Both prompt labels come from those destinations, so nothing here is bespoke.
 */
val NaggingThoughts = card("Nagging Thoughts") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top two cards of your library. Put one of them into your hand and the other into your graveyard.\n" +
        "Madness {1}{U} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(count = 2, keepCount = 1)
    }

    madness("{1}{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Seb McKinnon"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8a6d6e4-e91f-444f-9eed-88fceaf1a4b8.jpg?1783937793"
    }
}
