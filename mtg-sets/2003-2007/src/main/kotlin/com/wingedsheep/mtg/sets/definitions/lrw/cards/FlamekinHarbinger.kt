package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Flamekin Harbinger
 * {R}
 * Creature — Elemental Shaman
 * 1/1
 * When this creature enters, you may search your library for an Elemental card, reveal it, then
 * shuffle and put that card on top.
 */
val FlamekinHarbinger = card("Flamekin Harbinger") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, you may search your library for an Elemental card, " +
        "reveal it, then shuffle and put that card on top."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ELEMENTAL),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for an Elemental card, reveal it, then shuffle " +
            "and put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f330c0f4-13d2-4432-9ced-f044bef98ec8.jpg?1783942876"
    }
}
