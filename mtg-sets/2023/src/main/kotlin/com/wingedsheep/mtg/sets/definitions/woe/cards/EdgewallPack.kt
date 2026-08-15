package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Edgewall Pack
 * {3}{R}
 * Creature — Dog
 * 3/3
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * When this creature enters, create a 1/1 black Rat creature token with "This token can't block."
 *
 * The Rat is WOE's shared type-named token, so it comes from [woeRatToken] rather than being
 * re-declared here.
 */
val EdgewallPack = card("Edgewall Pack") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dog"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "When this creature enters, create a 1/1 black Rat creature token with \"This token can't block.\""
    power = 3
    toughness = 3

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = woeRatToken()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Leesha Hannigan"
        flavorText = "The mayor sent hounds to root out Lord Skitter, but their loyalty was easily " +
            "bought with a handful of bones."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/acda9d02-00fc-49e2-a9f3-176e9c0a8c5f.jpg?1783915097"
    }
}
