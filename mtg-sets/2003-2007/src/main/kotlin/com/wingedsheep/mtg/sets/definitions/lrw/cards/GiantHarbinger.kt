package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Giant Harbinger
 * {4}{R}
 * Creature — Giant Shaman
 * 3/4
 * When this creature enters, you may search your library for a Giant card, reveal it, then
 * shuffle and put that card on top.
 */
val GiantHarbinger = card("Giant Harbinger") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Shaman"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, you may search your library for a Giant card, reveal " +
        "it, then shuffle and put that card on top."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.GIANT),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Giant card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "169"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f72f461-98d7-45e0-9968-9afb240fbf1e.jpg?1783942876"
    }
}
