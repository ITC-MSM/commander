package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Kithkin Harbinger
 * {2}{W}
 * Creature — Kithkin Wizard
 * 1/3
 * When this creature enters, you may search your library for a Kithkin card, reveal it, then
 * shuffle and put that card on top.
 */
val KithkinHarbinger = card("Kithkin Harbinger") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Wizard"
    power = 1
    toughness = 3
    oracleText = "When this creature enters, you may search your library for a Kithkin card, reveal " +
        "it, then shuffle and put that card on top."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.KITHKIN),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Kithkin card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "26"
        artist = "Steve Prescott"
        flavorText = "Her ears are open to even the softest voice."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64735178-3dc5-4a95-92fa-e15bd04e5733.jpg?1783942912"
    }
}
