package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Demonic Bargain
 * {2}{B}
 * Sorcery
 *
 * Exile the top thirteen cards of your library, then search your library for a card. Put that card
 * into your hand, then shuffle.
 *
 * A tutor with a self-mill-into-exile tax: [Patterns.Library.exileTop]`(13)` removes the top
 * thirteen cards face down into exile, then [Patterns.Library.searchLibrary] finds any one card
 * ([GameObjectFilter.Any]) to hand and shuffles the library afterward. Because the exile happens first, the
 * searched-for card can no longer be among the exiled thirteen.
 */
val DemonicBargain = card("Demonic Bargain") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Exile the top thirteen cards of your library, then search your library for a " +
        "card. Put that card into your hand, then shuffle."

    spell {
        effect = Effects.Composite(
            listOf(
                Patterns.Library.exileTop(13),
                Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Any,
                    count = 1,
                    destination = SearchDestination.HAND,
                    shuffleAfter = true
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "103"
        artist = "Sam Guay"
        flavorText = "\"Do not try to negotiate with me, human. You want power? This is the price.\"\n—Ormendahl"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80c3741e-cf04-4aa2-a6a9-ce19f043b22c.jpg?1783924868"
    }
}
