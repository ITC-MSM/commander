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
 * Merrow Harbinger
 * {3}{U}
 * Creature — Merfolk Wizard
 * 2/3
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 * When this creature enters, you may search your library for a Merfolk card, reveal it, then
 * shuffle and put that card on top.
 */
val MerrowHarbinger = card("Merrow Harbinger") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 3
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls " +
        "an Island.)\nWhen this creature enters, you may search your library for a Merfolk card, " +
        "reveal it, then shuffle and put that card on top."

    keywords(Keyword.ISLANDWALK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.MERFOLK),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Merfolk card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "73"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b47af11c-1090-4f0a-8eea-64a3b639e535.jpg?1783942900"
    }
}
