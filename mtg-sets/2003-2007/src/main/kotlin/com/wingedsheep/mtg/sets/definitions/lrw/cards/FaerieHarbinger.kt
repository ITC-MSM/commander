package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Faerie Harbinger
 * {3}{U}
 * Creature — Faerie Wizard
 * 2/2
 * Flash
 * Flying
 * When this creature enters, you may search your library for a Faerie card, reveal it, then
 * shuffle and put that card on top.
 */
val FaerieHarbinger = card("Faerie Harbinger") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Wizard"
    power = 2
    toughness = 2
    oracleText = "Flash\nFlying\nWhen this creature enters, you may search your library for a Faerie " +
        "card, reveal it, then shuffle and put that card on top."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.FAERIE),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Faerie card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Larry MacDougall"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9bdc319-4e06-420b-ba54-6cb994d4e279.jpg?1783942903"
    }
}
