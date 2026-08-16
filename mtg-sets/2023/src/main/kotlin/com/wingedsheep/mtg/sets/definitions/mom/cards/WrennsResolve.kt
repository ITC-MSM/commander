package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry

/**
 * Wrenn's Resolve
 * {1}{R}
 * Sorcery
 * Exile the top two cards of your library. Until the end of your next turn, you may play those cards.
 */
val WrennsResolve = card("Wrenn's Resolve") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Exile the top two cards of your library. Until the end of your next turn, you " +
        "may play those cards."

    spell {
        effect = Patterns.Exile.impulse(2, MayPlayExpiry.UntilEndOfNextTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "173"
        artist = "Viko Menezes"
        flavorText = "Even as she burned, Wrenn bent Realmbreaker to her will."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a47999c-12d5-4e1a-a9c1-40a1757007f1.jpg?1783916978"
        ruling(
            "2023-04-14",
            "The cards you play from exile follow the usual timing restrictions, and you must pay " +
                "any costs for spells you cast."
        )
        ruling("2023-04-14", "Any cards you don't play will remain in exile.")
    }
}
